/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package android.view;

import android.graphics.Rect;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOplusWindowManager extends IInterface {
    String DESCRIPTOR = "android.view.IOplusWindowManager";

    void requestKeyguard(String command) throws RemoteException;

    void registerOplusWindowStateObserver(IOplusWindowStateObserver observer)
            throws RemoteException;

    void unregisterOplusWindowStateObserver(IOplusWindowStateObserver observer)
            throws RemoteException;

    void getFocusedWindowFrame(Rect frame) throws RemoteException;

    boolean setPreferredDisplayMode(int modeId) throws RemoteException;

    abstract class Stub {
        private Stub() {
        }

        public static IOplusWindowManager asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof IOplusWindowManager) {
                return (IOplusWindowManager) local;
            }
            return new Proxy(binder);
        }

        private static class Proxy implements IOplusWindowManager {
            private static final int TRANSACTION_requestKeyguard = 14;
            private static final int TRANSACTION_registerOplusWindowStateObserver = 21;
            private static final int TRANSACTION_unregisterOplusWindowStateObserver = 22;
            private static final int TRANSACTION_getFocusedWindowFrame = 54;
            private static final int TRANSACTION_setPreferredDisplayMode = 86;

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public void requestKeyguard(String command) throws RemoteException {
                Parcel data = Parcel.obtain(asBinder());
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(command);
                    mRemote.transact(TRANSACTION_requestKeyguard, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void registerOplusWindowStateObserver(
                    IOplusWindowStateObserver observer) throws RemoteException {
                Parcel data = Parcel.obtain(asBinder());
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeStrongInterface(observer);
                    mRemote.transact(TRANSACTION_registerOplusWindowStateObserver,
                            data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void unregisterOplusWindowStateObserver(
                    IOplusWindowStateObserver observer) throws RemoteException {
                Parcel data = Parcel.obtain(asBinder());
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeStrongInterface(observer);
                    mRemote.transact(TRANSACTION_unregisterOplusWindowStateObserver,
                            data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void getFocusedWindowFrame(Rect frame) throws RemoteException {
                Parcel data = Parcel.obtain(asBinder());
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_getFocusedWindowFrame, data, reply, 0);
                    reply.readException();
                    if (reply.readInt() != 0) {
                        frame.readFromParcel(reply);
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public boolean setPreferredDisplayMode(int modeId) throws RemoteException {
                Parcel data = Parcel.obtain(asBinder());
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(modeId);
                    mRemote.transact(TRANSACTION_setPreferredDisplayMode, data, reply, 0);
                    reply.readException();
                    return reply.readBoolean();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
