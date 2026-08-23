/*
 * Copyright (C) 2026 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package android.bluetooth;

import android.bluetooth.le.ScanResult;

/** Callback surface used by Oplus applications for enhanced Bluetooth RSSI detection. */
public abstract class OplusBluetoothRssiDetectCallback {

    public void onRssiDetectResultCallback(ScanResult result, float modifiedRssi) {
    }

    public void onRssiDetectDistanceCallback(
            ScanResult result, float modifiedRssi, float distance) {
    }
}
