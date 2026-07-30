/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.content;

public class Intent {
    public static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 0x01000000;
    public static final String ACTION_CALL_PRIVILEGED =
            "android.intent.action.CALL_PRIVILEGED";
    public static final String EXTRA_USER_ID = "android.intent.extra.USER_ID";
    public static final String EXTRA_TASK_ID = "android.intent.extra.TASK_ID";
    public static final String EXTRA_SIM_STATE = "ss";
    public static final String SIM_STATE_LOADED = "LOADED";
    public static final String SIM_STATE_ABSENT = "ABSENT";
    public static final String ACTION_USER_STARTED = "android.intent.action.USER_STARTED";
    public static final String ACTION_USER_STOPPED = "android.intent.action.USER_STOPPED";
    public static final String ACTION_OVERLAY_CHANGED =
            "android.intent.action.OVERLAY_CHANGED";
    public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
    public static final String ACTION_PREFERRED_ACTIVITY_CHANGED =
            "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED";
    public static final String ACTION_USER_REMOVED = "android.intent.action.USER_REMOVED";
    public static final String ACTION_PRE_BOOT_COMPLETED =
            "android.intent.action.PRE_BOOT_COMPLETED";

    private final android.content.Intent mTarget;

    public Intent(android.content.Intent target) {
        mTarget = target;
    }

    public static void prepareToLeaveUser(android.content.Intent target, int userId) {
        target.prepareToLeaveUser(userId);
    }

    public static void fixUris(android.content.Intent target, int contentUserHint) {
        target.fixUris(contentUserHint);
    }

    public void prepareToLeaveUser(int userId) {
        mTarget.prepareToLeaveUser(userId);
    }

    public void fixUris(int contentUserHint) {
        mTarget.fixUris(contentUserHint);
    }
}
