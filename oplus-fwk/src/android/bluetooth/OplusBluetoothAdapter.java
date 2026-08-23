package android.bluetooth;

import android.bluetooth.le.ScanFilter;

import java.util.List;

public class OplusBluetoothAdapter {

    private static OplusBluetoothAdapter sAdapter;

    public static OplusBluetoothAdapter getOplusBluetoothAdapter() {
        if (sAdapter == null) {
            sAdapter = new OplusBluetoothAdapter();
        }
        return sAdapter;
    }

    /**
     * Registers an Oplus RSSI callback.
     *
     * <p>The AOSP Bluetooth service does not expose the Oplus binder extension that drives this
     * callback. Keep the API available to Oplus clients and report that registration is not
     * supported instead of failing class verification.</p>
     */
    public boolean registerOplusBluetoothRssiDetectCallback(
            OplusBluetoothRssiDetectCallback callback) {
        return false;
    }

    /** See {@link #registerOplusBluetoothRssiDetectCallback}. */
    public boolean unregisterOplusBluetoothRssiDetectCallback(
            OplusBluetoothRssiDetectCallback callback) {
        return false;
    }

    /**
     * Registers an Oplus RSSI callback with LE scan filters.
     *
     * <p>Filtered RSSI detection is implemented by the Oplus Bluetooth service extension on
     * stock software. The compatibility framework intentionally does not start a second LE scan
     * on behalf of callers.</p>
     */
    public boolean registerFilteredBluetoothRssiDetectCallback(
            OplusBluetoothRssiDetectCallback callback, List<ScanFilter> filters) {
        return false;
    }

    /**
     * Requests the vendor page-scan interval.
     *
     * <p>This requires a controller command exposed by the Oplus Bluetooth service. Returning
     * false lets clients fall back to normal Android Bluetooth behavior.</p>
     */
    public boolean setPageScanInterval(int interval) {
        return false;
    }
}
