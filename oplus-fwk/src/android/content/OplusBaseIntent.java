/*
 * Copyright (C) 2026 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package android.content;

/**
 * Compatibility surface for the additional flags carried by an OxygenOS {@link Intent}.
 *
 * <p>AOSP {@code Intent} does not inherit from this class. Oplus compatibility libraries check
 * the relationship before calling these methods, so retaining the flags here is useful for OEM
 * subclasses while ordinary AOSP intents safely use their standard flags.</p>
 */
public class OplusBaseIntent {

    public static final int OPLUS_FLAG_ACTIVITY_CONTINUE_PRIVACY = 0x20000000;
    public static final int OPLUS_FLAG_ACTIVITY_CONTINUE_REQUIRED = 0x10000000;

    private int mOplusFlags;

    public int getOplusFlags() {
        return mOplusFlags;
    }

    public void setOplusFlags(int oplusFlags) {
        mOplusFlags = oplusFlags;
    }

    public void addOplusFlags(int oplusFlags) {
        mOplusFlags |= oplusFlags;
    }

    public void removeOplusFlags(int oplusFlags) {
        mOplusFlags &= ~oplusFlags;
    }
}
