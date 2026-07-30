/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.os;

import android.util.ArraySet;

public class Parcel {
    private final android.os.Parcel mParcel;

    public Parcel(android.os.Parcel parcel) {
        mParcel = parcel;
    }

    public static String[] readStringArray(android.os.Parcel target) {
        return target.readStringArray();
    }

    public static ArraySet<?> readArraySet(android.os.Parcel target, ClassLoader loader) {
        return target.readArraySet(loader);
    }

    public static void writeArraySet(android.os.Parcel target, ArraySet<?> value) {
        target.writeArraySet(value);
    }

    public static String readStringNoHelper(android.os.Parcel target) {
        return target.readStringNoHelper();
    }

    public static void writeStringNoHelper(android.os.Parcel target, String value) {
        target.writeStringNoHelper(value);
    }

    public static String readString16NoHelper(android.os.Parcel target) {
        return target.readString16NoHelper();
    }

    public static void writeCharSequence(android.os.Parcel target, CharSequence value) {
        target.writeCharSequence(value);
    }

    public static CharSequence readCharSequence(android.os.Parcel target) {
        return target.readCharSequence();
    }

    public final String[] readStringArray() {
        return mParcel.readStringArray();
    }

    public ArraySet<?> readArraySet(ClassLoader loader) {
        return mParcel.readArraySet(loader);
    }

    public void writeArraySet(ArraySet<?> value) {
        mParcel.writeArraySet(value);
    }

    public String readStringNoHelper() {
        return mParcel.readStringNoHelper();
    }

    public void writeStringNoHelper(String value) {
        mParcel.writeStringNoHelper(value);
    }

    public String readString16NoHelper() {
        return mParcel.readString16NoHelper();
    }

    public final void writeCharSequence(CharSequence value) {
        mParcel.writeCharSequence(value);
    }

    public final CharSequence readCharSequence() {
        return mParcel.readCharSequence();
    }
}
