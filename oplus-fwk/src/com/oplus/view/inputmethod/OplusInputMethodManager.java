/*
 * Copyright (C) 2026 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.view.inputmethod;

import android.content.ComponentName;

/** Compatibility facade for OxygenOS input-method synergy APIs. */
public final class OplusInputMethodManager {

    private OplusInputMethodManager() {
    }

    private static final class InstanceHolder {
        private static final OplusInputMethodManager INSTANCE = new OplusInputMethodManager();
    }

    public static OplusInputMethodManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void registerInputMethodSynergyService(ComponentName synergyName, boolean register) {
    }

    public void commitTextByOtherSide() {
    }
}
