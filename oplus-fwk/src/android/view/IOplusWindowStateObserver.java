/*
 * Copyright (C) 2026 The Infinity-X Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package android.view;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOplusWindowStateObserver extends IInterface {
    String DESCRIPTOR = "android.view.IOplusWindowStateObserver";

    void onWindowStateChange(Bundle options) throws RemoteException;

    class Default implements IOplusWindowStateObserver {
        @Override
        public void onWindowStateChange(Bundle options) throws RemoteException {
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    abstract class Stub extends Binder implements IOplusWindowStateObserver {
        static final int TRANSACTION_onWindowStateChange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOplusWindowStateObserver asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof IOplusWindowStateObserver) {
                return (IOplusWindowStateObserver) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            return transactionCode == TRANSACTION_onWindowStateChange
                    ? "onWindowStateChange" : null;
        }

        @Override
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            if (code >= IBinder.FIRST_CALL_TRANSACTION
                    && code <= IBinder.LAST_CALL_TRANSACTION) {
                data.enforceInterface(DESCRIPTOR);
            }
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            if (code == TRANSACTION_onWindowStateChange) {
                Bundle options = data.readTypedObject(Bundle.CREATOR);
                data.enforceNoDataAvail();
                onWindowStateChange(options);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IOplusWindowStateObserver {
            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public void onWindowStateChange(Bundle options) throws RemoteException {
                Parcel data = Parcel.obtain(asBinder());
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeTypedObject(options, 0);
                    mRemote.transact(TRANSACTION_onWindowStateChange, data, null,
                            IBinder.FLAG_ONEWAY);
                } finally {
                    data.recycle();
                }
            }
        }

        @Override
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
