/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.content;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.UserHandle;

import java.io.File;

public class Context {
    public static final int BIND_FOREGROUND_SERVICE_WHILE_AWAKE = 0x02000000;
    public static final int BIND_FOREGROUND_SERVICE = 0x04000000;
    public static final String VOICE_INTERACTION_MANAGER_SERVICE = "voiceinteraction";
    public static final int RECEIVER_EXPORTED = 2;
    public static final String ACTIVITY_TASK_SERVICE = "activity_task";
    public static final String DEVICE_STATE_SERVICE = "device_state";

    private final android.content.Context mContext;

    public Context(android.content.Context context) {
        mContext = context;
    }

    public static File getSharedPreferencesPath(android.content.Context target, String name) {
        return target.getSharedPreferencesPath(name);
    }

    public static int getDisplayId(android.content.Context target) {
        return target.getDisplayId();
    }

    public static android.content.Context createCredentialProtectedStorageContext(
            android.content.Context target) {
        return target.createCredentialProtectedStorageContext();
    }

    public static int getThemeResId(android.content.Context target) {
        return target.getThemeResId();
    }

    public static android.content.Context createPackageContextAsUser(
            android.content.Context target, String packageName, int flags, UserHandle user)
            throws PackageManager.NameNotFoundException {
        return target.createPackageContextAsUser(packageName, flags, user);
    }

    public static android.content.Context createApplicationContext(
            android.content.Context target, ApplicationInfo application, int flags)
            throws PackageManager.NameNotFoundException {
        return target.createApplicationContext(application, flags);
    }

    public static ComponentName startServiceAsUser(
            android.content.Context target, Intent service, UserHandle user) {
        return target.startServiceAsUser(service, user);
    }

    public static Intent registerReceiverForAllUsers(
            android.content.Context target,
            BroadcastReceiver receiver,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler) {
        return target.registerReceiverForAllUsers(
                receiver, filter, broadcastPermission, scheduler);
    }

    public static void startActivityAsUser(
            android.content.Context target, Intent intent, UserHandle user) {
        target.startActivityAsUser(intent, user);
    }

    public static ComponentName startForegroundServiceAsUser(
            android.content.Context target, Intent service, UserHandle user) {
        return target.startForegroundServiceAsUser(service, user);
    }

    public static Intent registerReceiverAsUser(
            android.content.Context target,
            BroadcastReceiver receiver,
            UserHandle user,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler) {
        return target.registerReceiverAsUser(
                receiver, user, filter, broadcastPermission, scheduler);
    }

    public static Intent registerReceiverAsUser(
            android.content.Context target,
            BroadcastReceiver receiver,
            UserHandle user,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler,
            int flags) {
        return target.registerReceiverAsUser(
                receiver, user, filter, broadcastPermission, scheduler, flags);
    }

    public static Handler getMainThreadHandler(android.content.Context target) {
        return target.getMainThreadHandler();
    }

    public static int getUserId(android.content.Context target) {
        return target.getUserId();
    }

    public File getSharedPreferencesPath(String name) {
        return mContext.getSharedPreferencesPath(name);
    }

    public int getDisplayId() {
        return mContext.getDisplayId();
    }

    public android.content.Context createCredentialProtectedStorageContext() {
        return mContext.createCredentialProtectedStorageContext();
    }

    public int getThemeResId() {
        return mContext.getThemeResId();
    }

    public android.content.Context createPackageContextAsUser(
            String packageName, int flags, UserHandle user)
            throws PackageManager.NameNotFoundException {
        return mContext.createPackageContextAsUser(packageName, flags, user);
    }

    public android.content.Context createApplicationContext(
            ApplicationInfo application, int flags)
            throws PackageManager.NameNotFoundException {
        return mContext.createApplicationContext(application, flags);
    }

    public ComponentName startServiceAsUser(Intent service, UserHandle user) {
        return mContext.startServiceAsUser(service, user);
    }

    public Intent registerReceiverForAllUsers(
            BroadcastReceiver receiver,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler) {
        return mContext.registerReceiverForAllUsers(
                receiver, filter, broadcastPermission, scheduler);
    }

    public void startActivityAsUser(Intent intent, UserHandle user) {
        mContext.startActivityAsUser(intent, user);
    }

    public ComponentName startForegroundServiceAsUser(Intent service, UserHandle user) {
        return mContext.startForegroundServiceAsUser(service, user);
    }

    public Intent registerReceiverAsUser(
            BroadcastReceiver receiver,
            UserHandle user,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler) {
        return mContext.registerReceiverAsUser(
                receiver, user, filter, broadcastPermission, scheduler);
    }

    public Intent registerReceiverAsUser(
            BroadcastReceiver receiver,
            UserHandle user,
            IntentFilter filter,
            String broadcastPermission,
            Handler scheduler,
            int flags) {
        return mContext.registerReceiverAsUser(
                receiver, user, filter, broadcastPermission, scheduler, flags);
    }

    public Handler getMainThreadHandler() {
        return mContext.getMainThreadHandler();
    }
}
