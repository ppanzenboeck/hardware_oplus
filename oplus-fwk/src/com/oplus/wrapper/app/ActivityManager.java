/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.app;

public class ActivityManager {
    public static final int START_TASK_TO_FRONT = 2;
    public static final int START_SUCCESS = 0;
    public static final int START_DELIVERED_TO_TOP = 3;

    private final android.app.ActivityManager mActivityManager;

    @Deprecated
    public ActivityManager(android.app.ActivityManager activityManager) {
        mActivityManager = activityManager;
    }

    public static int getCurrentUser() {
        return android.app.ActivityManager.getCurrentUser();
    }

    public static void forceStopPackage(
            android.app.ActivityManager activityManager, String packageName) {
        activityManager.forceStopPackage(packageName);
    }

    @Deprecated
    public void forceStopPackage(String packageName) {
        mActivityManager.forceStopPackage(packageName);
    }

    public static boolean isHighEndGfx() {
        return android.app.ActivityManager.isHighEndGfx();
    }
}
