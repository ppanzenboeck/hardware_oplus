package android.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class OplusWindowManager {
    private static final String TAG = "OplusWindowManager";

    private static IOplusWindowManager getService() {
        try {
            IBinder windowBinder = ServiceManager.getService(Context.WINDOW_SERVICE);
            IWindowManager windowManager = IWindowManager.Stub.asInterface(windowBinder);
            if (windowManager == null) {
                return null;
            }
            return IOplusWindowManager.Stub.asInterface(
                    windowManager.asBinder().getExtension());
        } catch (Exception e) {
            Log.e(TAG, "Unable to get WindowManager extension", e);
            return null;
        }
    }

    public OplusWindowManager() {
    }

    public void requestKeyguard(String command) throws RemoteException {
        IOplusWindowManager service = getService();
        if (service != null) {
            service.requestKeyguard(command);
        }
    }

    public boolean setPreferredDisplayMode(int mode) throws RemoteException {
        IOplusWindowManager service = getService();
        return service != null && service.setPreferredDisplayMode(mode);
    }

    public void registerOplusWindowStateObserver(IOplusWindowStateObserver observer)
            throws RemoteException {
        IOplusWindowManager service = getService();
        if (service != null) {
            service.registerOplusWindowStateObserver(observer);
        }
    }

    public void unregisterOplusWindowStateObserver(IOplusWindowStateObserver observer)
            throws RemoteException {
        IOplusWindowManager service = getService();
        if (service != null) {
            service.unregisterOplusWindowStateObserver(observer);
        }
    }

    public void getFocusedWindowFrame(Rect frame) throws RemoteException {
        IOplusWindowManager service = getService();
        if (service != null) {
            service.getFocusedWindowFrame(frame);
        }
    }
}
