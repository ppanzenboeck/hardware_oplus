/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.content;

import android.database.ContentObserver;
import android.net.Uri;

public class ContentResolver {
    public static final int NOTIFY_NO_DELAY = 0x8000;

    private final android.content.ContentResolver mContentResolver;

    @Deprecated
    public ContentResolver(android.content.ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public static void registerContentObserver(
            android.content.ContentResolver target,
            Uri uri,
            boolean notifyForDescendents,
            ContentObserver observer,
            int userHandle) {
        target.registerContentObserver(
                uri, notifyForDescendents, observer, userHandle);
    }

    @Deprecated
    public void registerContentObserver(
            Uri uri,
            boolean notifyForDescendents,
            ContentObserver observer,
            int userHandle) {
        mContentResolver.registerContentObserver(
                uri, notifyForDescendents, observer, userHandle);
    }
}
