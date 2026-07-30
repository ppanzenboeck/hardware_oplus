/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.service.attention;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IProximityUpdateCallback {
    void onProximityUpdate(double distance) throws RemoteException;

    abstract class Stub implements IInterface, IProximityUpdateCallback {
        private final android.service.attention.IProximityUpdateCallback mTarget =
                new android.service.attention.IProximityUpdateCallback.Stub() {
                    @Override
                    public void onProximityUpdate(double distance) throws RemoteException {
                        IProximityUpdateCallback.Stub.this.onProximityUpdate(distance);
                    }
                };

        public static IProximityUpdateCallback asInterface(IBinder binder) {
            return new Proxy(
                    android.service.attention.IProximityUpdateCallback.Stub.asInterface(binder));
        }

        @Override
        public IBinder asBinder() {
            return mTarget.asBinder();
        }

        private static class Proxy implements IProximityUpdateCallback {
            private final android.service.attention.IProximityUpdateCallback mTarget;

            Proxy(android.service.attention.IProximityUpdateCallback target) {
                mTarget = target;
            }

            @Override
            public void onProximityUpdate(double distance) throws RemoteException {
                mTarget.onProximityUpdate(distance);
            }
        }
    }
}
