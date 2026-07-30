/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.service.attention;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IAttentionCallback {
    void onSuccess(int result, long timestamp) throws RemoteException;

    void onFailure(int error) throws RemoteException;

    abstract class Stub implements IInterface, IAttentionCallback {
        private final android.service.attention.IAttentionCallback mTarget =
                new android.service.attention.IAttentionCallback.Stub() {
                    @Override
                    public void onSuccess(int result, long timestamp) throws RemoteException {
                        IAttentionCallback.Stub.this.onSuccess(result, timestamp);
                    }

                    @Override
                    public void onFailure(int error) throws RemoteException {
                        IAttentionCallback.Stub.this.onFailure(error);
                    }
                };

        public static IAttentionCallback asInterface(IBinder binder) {
            return new Proxy(android.service.attention.IAttentionCallback.Stub.asInterface(binder));
        }

        @Override
        public IBinder asBinder() {
            return mTarget.asBinder();
        }

        private static class Proxy implements IAttentionCallback {
            private final android.service.attention.IAttentionCallback mTarget;

            Proxy(android.service.attention.IAttentionCallback target) {
                mTarget = target;
            }

            @Override
            public void onSuccess(int result, long timestamp) throws RemoteException {
                mTarget.onSuccess(result, timestamp);
            }

            @Override
            public void onFailure(int error) throws RemoteException {
                mTarget.onFailure(error);
            }
        }
    }
}
