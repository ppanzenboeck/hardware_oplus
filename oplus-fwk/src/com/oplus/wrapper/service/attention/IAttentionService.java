/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oplus.wrapper.service.attention;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IAttentionService {
    void checkAttention(IAttentionCallback callback) throws RemoteException;

    void cancelAttentionCheck(IAttentionCallback callback) throws RemoteException;

    void onStartProximityUpdates(IProximityUpdateCallback callback) throws RemoteException;

    void onStopProximityUpdates() throws RemoteException;

    abstract class Stub implements IInterface, IAttentionService {
        private final android.service.attention.IAttentionService mTarget =
                new android.service.attention.IAttentionService.Stub() {
                    @Override
                    public void checkAttention(
                            final android.service.attention.IAttentionCallback callback)
                            throws RemoteException {
                        IAttentionService.Stub.this.checkAttention(
                                wrapAttentionCallback(callback));
                    }

                    @Override
                    public void cancelAttentionCheck(
                            final android.service.attention.IAttentionCallback callback)
                            throws RemoteException {
                        IAttentionService.Stub.this.cancelAttentionCheck(
                                wrapAttentionCallback(callback));
                    }

                    @Override
                    public void onStartProximityUpdates(
                            final android.service.attention.IProximityUpdateCallback callback)
                            throws RemoteException {
                        IAttentionService.Stub.this.onStartProximityUpdates(
                                wrapProximityCallback(callback));
                    }

                    @Override
                    public void onStopProximityUpdates() throws RemoteException {
                        IAttentionService.Stub.this.onStopProximityUpdates();
                    }
                };

        public static IAttentionService asInterface(IBinder binder) {
            return new Proxy(android.service.attention.IAttentionService.Stub.asInterface(binder));
        }

        @Override
        public IBinder asBinder() {
            return mTarget.asBinder();
        }

        private static IAttentionCallback wrapAttentionCallback(
                final android.service.attention.IAttentionCallback callback) {
            return new IAttentionCallback.Stub() {
                @Override
                public void onSuccess(int result, long timestamp) throws RemoteException {
                    if (callback != null) {
                        callback.onSuccess(result, timestamp);
                    }
                }

                @Override
                public void onFailure(int error) throws RemoteException {
                    if (callback != null) {
                        callback.onFailure(error);
                    }
                }
            };
        }

        private static IProximityUpdateCallback wrapProximityCallback(
                final android.service.attention.IProximityUpdateCallback callback) {
            return new IProximityUpdateCallback.Stub() {
                @Override
                public void onProximityUpdate(double distance) throws RemoteException {
                    if (callback != null) {
                        callback.onProximityUpdate(distance);
                    }
                }
            };
        }

        private static class Proxy implements IAttentionService {
            private final android.service.attention.IAttentionService mTarget;

            Proxy(android.service.attention.IAttentionService target) {
                mTarget = target;
            }

            @Override
            public void checkAttention(final IAttentionCallback callback) throws RemoteException {
                mTarget.checkAttention(new android.service.attention.IAttentionCallback.Stub() {
                    @Override
                    public void onSuccess(int result, long timestamp) throws RemoteException {
                        if (callback != null) {
                            callback.onSuccess(result, timestamp);
                        }
                    }

                    @Override
                    public void onFailure(int error) throws RemoteException {
                        if (callback != null) {
                            callback.onFailure(error);
                        }
                    }
                });
            }

            @Override
            public void cancelAttentionCheck(final IAttentionCallback callback)
                    throws RemoteException {
                mTarget.cancelAttentionCheck(
                        new android.service.attention.IAttentionCallback.Stub() {
                            @Override
                            public void onSuccess(int result, long timestamp)
                                    throws RemoteException {
                                if (callback != null) {
                                    callback.onSuccess(result, timestamp);
                                }
                            }

                            @Override
                            public void onFailure(int error) throws RemoteException {
                                if (callback != null) {
                                    callback.onFailure(error);
                                }
                            }
                        });
            }

            @Override
            public void onStartProximityUpdates(final IProximityUpdateCallback callback)
                    throws RemoteException {
                mTarget.onStartProximityUpdates(
                        new android.service.attention.IProximityUpdateCallback.Stub() {
                            @Override
                            public void onProximityUpdate(double distance)
                                    throws RemoteException {
                                if (callback != null) {
                                    callback.onProximityUpdate(distance);
                                }
                            }
                        });
            }

            @Override
            public void onStopProximityUpdates() throws RemoteException {
                mTarget.onStopProximityUpdates();
            }
        }
    }
}
