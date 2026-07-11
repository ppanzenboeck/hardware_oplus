package com.oplus.framework.flags;

/**
 * Compatibility shim for generated Oplus framework feature flags.
 *
 * Stock Oplus builds provide this class from the framework bootclasspath. Some
 * ported Oplus apps link against it directly, so keep the referenced methods
 * available even when the backing flag infrastructure is not present.
 */
public final class Flags {
    private Flags() {
    }

    public static boolean applicationSharedMemoryEnabled() {
        return false;
    }

    public static boolean cacheSdkSystemFeatures() {
        return false;
    }

    public static boolean coloros1600ConfidentialOdc() {
        return true;
    }

    public static boolean enableConnectedDisplaysWallpaper() {
        return false;
    }

    public static boolean preventIntentRedirect() {
        return true;
    }

    public static boolean supportsMultiInstanceSystemUi() {
        return false;
    }
}
