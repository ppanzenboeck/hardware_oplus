/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "vendor.lineage.touch-service.oplus"

#include "GloveMode.h"
#include "HighTouchPollingRate.h"
#include "TouchscreenGesture.h"

#include <android-base/logging.h>
#include <android-base/properties.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>

#include <chrono>
#include <thread>

using aidl::vendor::lineage::touch::GloveMode;
using aidl::vendor::lineage::touch::HighTouchPollingRate;
using aidl::vendor::lineage::touch::TouchscreenGesture;
using aidl::vendor::oplus::hardware::touch::IOplusTouch;

namespace {

constexpr char kTouchReportRateProperty[] = "sys.touch.report_rate";
constexpr char kPersistTouchReportRateProperty[] = "persist.hbp.touch_report_rate";
constexpr char kBootCompletedProperty[] = "sys.boot_completed";

}  // namespace

int main() {
    ABinderProcess_setThreadPoolMaxThreadCount(0);

    const std::string instance = std::string() + IOplusTouch::descriptor + "/default";
    std::shared_ptr<IOplusTouch> oplusTouch =
            USE_OPLUSTOUCH ? IOplusTouch::fromBinder(ndk::SpAIBinder(
                                     AServiceManager_waitForService(instance.c_str())))
                           : nullptr;

    std::shared_ptr<GloveMode> gm =
            ENABLE_GM ? ndk::SharedRefBase::make<GloveMode>(oplusTouch) : nullptr;
    std::shared_ptr<HighTouchPollingRate> htpr =
            ENABLE_HTPR ? ndk::SharedRefBase::make<HighTouchPollingRate>(oplusTouch) : nullptr;
    std::shared_ptr<TouchscreenGesture> tg =
            ENABLE_TG ? ndk::SharedRefBase::make<TouchscreenGesture>(oplusTouch) : nullptr;

    if (gm) {
        const std::string instance = std::string(GloveMode::descriptor) + "/default";
        const binder_status_t status =
                AServiceManager_addService(gm->asBinder().get(), instance.c_str());
        CHECK_EQ(status, STATUS_OK) << "Failed to add service " << instance << " " << status;
    }

    if (htpr) {
        const std::string instance = std::string(HighTouchPollingRate::descriptor) + "/default";
        const binder_status_t status =
                AServiceManager_addService(htpr->asBinder().get(), instance.c_str());
        CHECK_EQ(status, STATUS_OK) << "Failed to add service " << instance << " " << status;

        std::thread([htpr] {
            if (!android::base::WaitForProperty(kBootCompletedProperty, "1",
                                                std::chrono::seconds(120))) {
                LOG(ERROR) << "Timed out waiting to restore the touch report rate";
                return;
            }

            const int mode = android::base::GetIntProperty(
                    kPersistTouchReportRateProperty,
                    android::base::GetIntProperty(kTouchReportRateProperty, 0));
            const ndk::ScopedAStatus restoreStatus = htpr->setEnabled(mode != 0);
            if (!restoreStatus.isOk()) {
                LOG(ERROR) << "Failed to restore touch report rate mode " << mode;
            } else {
                LOG(INFO) << "Restored touch report rate mode " << mode;
            }
        }).detach();
    }

    if (tg) {
        const std::string instance = std::string(TouchscreenGesture::descriptor) + "/default";
        const binder_status_t status =
                AServiceManager_addService(tg->asBinder().get(), instance.c_str());
        CHECK_EQ(status, STATUS_OK) << "Failed to add service " << instance << " " << status;
    }

    ABinderProcess_joinThreadPool();
    return EXIT_FAILURE;  // should not reach
}
