package android.bluetooth;

/** Stub for OEM bluetooth-device extension. OplusCamera reads getOplusBluetoothClass(). No-op. */
public class OplusBluetoothDevice {
    public OplusBluetoothDevice(BluetoothDevice device) {
    }

    public int getOplusBluetoothClass() {
        return 0;
    }

    /**
     * Reports vendor audio-delay parameters for a remote device.
     *
     * <p>The corresponding Oplus Bluetooth service extension is not part of AOSP. Expose the API
     * so Oplus clients can fall back cleanly when the service is unavailable.</p>
     */
    public boolean setRemoteDelayReport(String[] configKeys, int[] configValues) {
        return false;
    }
}
