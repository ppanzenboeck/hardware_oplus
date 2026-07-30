/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.provider;

import android.content.ContentResolver;

public class Settings {
    private Settings() {
    }

    public static class Global {
        public static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
        public static final int ZEN_MODE_OFF = 0;
        public static final String DEVICE_PROVISIONING_MOBILE_DATA_ENABLED =
                "device_provisioning_mobile_data";
        public static final String LOW_POWER_MODE = "low_power";
        public static final String DEVELOPMENT_ENABLE_FREEFORM_WINDOWS_SUPPORT =
                "enable_freeform_support";
        public static final String ZEN_MODE = "zen_mode";

        private Global() {
        }

        public static boolean putString(
                ContentResolver resolver,
                String name,
                String value,
                String tag,
                boolean makeDefault,
                boolean overrideableByRestore) {
            return android.provider.Settings.Global.putString(
                    resolver, name, value, tag, makeDefault, overrideableByRestore);
        }
    }

    public static class Secure {
        public static final int LOCATION_CHANGER_SYSTEM_SETTINGS = 1;
        public static final String LOCATION_CHANGER = "location_changer";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED =
                "accessibility_display_magnification_enabled";
        public static final String MANAGED_PROVISIONING_DPC_DOWNLOADED =
                "managed_provisioning_dpc_downloaded";
        public static final String USER_SETUP_COMPLETE = "user_setup_complete";
        public static final String DOZE_ALWAYS_ON = "doze_always_on";
        public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
        public static final String LAUNCHER_TASKBAR_EDUCATION_SHOWING =
                "launcher_taskbar_education_showing";
        public static final String NAV_BAR_KIDS_MODE = "nav_bar_kids_mode";
        public static final String NAV_BAR_FORCE_VISIBLE = "nav_bar_force_visible";
        public static final String ONE_HANDED_MODE_ENABLED = "one_handed_mode_enabled";
        public static final String NUM_ROTATION_SUGGESTIONS_ACCEPTED =
                "num_rotation_suggestions_accepted";
        public static final String ONE_HANDED_MODE_ACTIVATED = "one_handed_mode_activated";
        public static final String ACCESSIBILITY_HIGH_TEXT_CONTRAST_ENABLED =
                "high_text_contrast_enabled";

        private Secure() {
        }

        public static int getIntForUser(
                ContentResolver resolver, String name, int userHandle)
                throws android.provider.Settings.SettingNotFoundException {
            return android.provider.Settings.Secure.getIntForUser(
                    resolver, name, userHandle);
        }

        public static int getIntForUser(
                ContentResolver resolver, String name, int def, int userHandle) {
            return android.provider.Settings.Secure.getIntForUser(
                    resolver, name, def, userHandle);
        }

        public static boolean putStringForUser(
                ContentResolver resolver, String name, String value, int userHandle) {
            return android.provider.Settings.Secure.putStringForUser(
                    resolver, name, value, userHandle);
        }

        public static String getStringForUser(
                ContentResolver resolver, String name, int userHandle) {
            return android.provider.Settings.Secure.getStringForUser(
                    resolver, name, userHandle);
        }

        public static boolean putIntForUser(
                ContentResolver resolver, String name, int value, int userHandle) {
            return android.provider.Settings.Secure.putIntForUser(
                    resolver, name, value, userHandle);
        }

        public static long getLongForUser(
                ContentResolver resolver, String name, long def, int userHandle) {
            return android.provider.Settings.Secure.getLongForUser(
                    resolver, name, def, userHandle);
        }

        public static float getFloatForUser(
                ContentResolver resolver, String name, float def, int userHandle) {
            return android.provider.Settings.Secure.getFloatForUser(
                    resolver, name, def, userHandle);
        }
    }

    public static class System {
        public static final String LOCK_TO_APP_ENABLED = "lock_to_app_enabled";

        private System() {
        }

        public static int getIntForUser(
                ContentResolver resolver, String name, int def, int userHandle) {
            return android.provider.Settings.System.getIntForUser(
                    resolver, name, def, userHandle);
        }

        public static int getIntForUser(
                ContentResolver resolver, String name, int userHandle)
                throws android.provider.Settings.SettingNotFoundException {
            return android.provider.Settings.System.getIntForUser(
                    resolver, name, userHandle);
        }

        public static boolean putIntForUser(
                ContentResolver resolver, String name, int value, int userHandle) {
            return android.provider.Settings.System.putIntForUser(
                    resolver, name, value, userHandle);
        }

        public static String getStringForUser(
                ContentResolver resolver, String name, int userHandle) {
            return android.provider.Settings.System.getStringForUser(
                    resolver, name, userHandle);
        }

        public static boolean putStringForUser(
                ContentResolver resolver, String name, String value, int userHandle) {
            return android.provider.Settings.System.putStringForUser(
                    resolver, name, value, userHandle);
        }

        public static long getLongForUser(
                ContentResolver resolver, String name, long def, int userHandle) {
            return android.provider.Settings.System.getLongForUser(
                    resolver, name, def, userHandle);
        }

        public static float getFloatForUser(
                ContentResolver resolver, String name, float def, int userHandle) {
            return android.provider.Settings.System.getFloatForUser(
                    resolver, name, def, userHandle);
        }
    }
}
