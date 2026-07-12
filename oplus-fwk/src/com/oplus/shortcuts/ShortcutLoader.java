package com.oplus.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.chooser.ChooserTarget;
import android.text.TextUtils;
import android.util.Log;
import com.oplus.shortcuts.chooser.DisplayResolveInfo;
import com.oplus.wrapper.app.prediction.AppPredictor;
import com.oplus.wrapper.app.prediction.AppTarget;
import com.oplus.wrapper.content.pm.ShortcutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ShortcutLoader {
    private static final Request NO_REQUEST = new Request(new DisplayResolveInfo[0]);
    private static final String TAG = "ChooserActivity";

    private final AtomicReference<Request> mActiveRequest = new AtomicReference<>(NO_REQUEST);
    private final AppPredictor mAppPredictor;
    private final AppPredictor.Callback mAppPredictorCallback;
    private final Executor mBackgroundExecutor;
    private final AtomicReference<Consumer<Result>> mCallback = new AtomicReference<>();
    private final Executor mCallbackExecutor;
    private final Context mContext;
    private final boolean mIsPersonalProfile;
    private final ShortcutToChooserTargetConverter mShortcutToChooserTargetConverter =
            new ShortcutToChooserTargetConverter();
    private final IntentFilter mTargetIntentFilter;
    private final UserHandle mUserHandle;
    private final UserManager mUserManager;

    public ShortcutLoader(
            Context context,
            AppPredictor appPredictor,
            UserHandle userHandle,
            IntentFilter targetIntentFilter,
            Consumer<Result> callback) {
        this(
                context,
                appPredictor,
                userHandle,
                true,
                targetIntentFilter,
                AsyncTask.SERIAL_EXECUTOR,
                context.getMainExecutor(),
                callback);
    }

    ShortcutLoader(
            Context context,
            AppPredictor appPredictor,
            UserHandle userHandle,
            boolean isPersonalProfile,
            IntentFilter targetIntentFilter,
            Executor backgroundExecutor,
            Executor callbackExecutor,
            Consumer<Result> callback) {
        mContext = context;
        mAppPredictor = appPredictor;
        mUserHandle = userHandle;
        mTargetIntentFilter = targetIntentFilter;
        mBackgroundExecutor = backgroundExecutor;
        mCallbackExecutor = callbackExecutor;
        mCallback.set(callback);
        mIsPersonalProfile = isPersonalProfile;
        mUserManager = context.getSystemService(UserManager.class);
        if (mAppPredictor != null) {
            mAppPredictorCallback = createAppPredictorCallback();
            mAppPredictor.registerPredictionUpdates(mCallbackExecutor, mAppPredictorCallback);
        } else {
            mAppPredictorCallback = null;
        }
    }

    public void destroy() {
        if (mCallback.getAndSet(null) != null && mAppPredictor != null) {
            mAppPredictor.unregisterPredictionUpdates(mAppPredictorCallback);
        }
    }

    public void queryShortcuts(DisplayResolveInfo[] appTargets) {
        if (isDestroyed()) {
            return;
        }
        mActiveRequest.set(new Request(appTargets));
        mBackgroundExecutor.execute(this::loadShortcuts);
    }

    private boolean isDestroyed() {
        return mCallback.get() == null;
    }

    private void loadShortcuts() {
        if (shouldQueryDirectShareTargets()) {
            Log.d(TAG, "querying direct share targets");
            queryDirectShareTargets(false);
        }
    }

    private void queryDirectShareTargets(boolean skipAppPredictionService) {
        if (isDestroyed()) {
            return;
        }
        if (!skipAppPredictionService && mAppPredictor != null) {
            mAppPredictor.requestPredictionUpdate();
            return;
        }
        if (mTargetIntentFilter == null) {
            return;
        }
        Context selectedProfileContext = mContext.createContextAsUser(mUserHandle, 0);
        android.content.pm.ShortcutManager shortcutManager =
                selectedProfileContext.getSystemService(android.content.pm.ShortcutManager.class);
        List<android.content.pm.ShortcutManager.ShareShortcutInfo> platformShortcuts =
                new ArrayList<>();
        if (shortcutManager != null) {
            try {
                platformShortcuts = shortcutManager.getShareTargets(mTargetIntentFilter);
            } catch (SecurityException e) {
                Log.w(TAG, "Unable to query direct share targets", e);
            }
        }
        List<ShortcutManager.ShareShortcutInfo> shortcuts =
                new ArrayList<>(platformShortcuts.size());
        for (android.content.pm.ShortcutManager.ShareShortcutInfo shortcut : platformShortcuts) {
            shortcuts.add(new ShortcutManager.ShareShortcutInfo(shortcut));
        }
        sendShareShortcutInfoList(shortcuts, false, null);
    }

    private AppPredictor.Callback createAppPredictorCallback() {
        return appPredictorTargets -> {
            if (appPredictorTargets.isEmpty() && shouldQueryDirectShareTargets()) {
                queryDirectShareTargets(true);
                return;
            }
            List<ShortcutManager.ShareShortcutInfo> shortcuts = new ArrayList<>();
            List<AppTarget> shortcutResults = new ArrayList<>();
            for (AppTarget appTarget : appPredictorTargets) {
                if (appTarget.getShortcutInfo() != null) {
                    shortcutResults.add(appTarget);
                }
            }
            for (AppTarget appTarget : shortcutResults) {
                shortcuts.add(
                        new ShortcutManager.ShareShortcutInfo(
                                new android.content.pm.ShortcutManager.ShareShortcutInfo(
                                        appTarget.getShortcutInfo(),
                                        new ComponentName(
                                                appTarget.getPackageName(), appTarget.getClassName()))));
            }
            sendShareShortcutInfoList(shortcuts, true, shortcutResults);
        };
    }

    private void sendShareShortcutInfoList(
            List<ShortcutManager.ShareShortcutInfo> shortcuts,
            boolean isFromAppPredictor,
            List<AppTarget> appPredictorTargets) {
        if (appPredictorTargets != null && appPredictorTargets.size() != shortcuts.size()) {
            throw new RuntimeException(
                    "resultList and appTargets must have the same size. resultList.size()="
                            + shortcuts.size()
                            + " appTargets.size()="
                            + appPredictorTargets.size());
        }

        Context selectedProfileContext = mContext.createContextAsUser(mUserHandle, 0);
        for (int i = shortcuts.size() - 1; i >= 0; i--) {
            String packageName = shortcuts.get(i).getTargetComponent().getPackageName();
            if (!isPackageEnabled(selectedProfileContext, packageName)) {
                shortcuts.remove(i);
                if (appPredictorTargets != null) {
                    appPredictorTargets.remove(i);
                }
            }
        }

        HashMap<ChooserTarget, AppTarget> directShareAppTargetCache = new HashMap<>();
        HashMap<ChooserTarget, ShortcutInfo> directShareShortcutInfoCache = new HashMap<>();
        DisplayResolveInfo[] appTargets = mActiveRequest.get().appTargets;
        List<ShortcutResultInfo> resultRecords = new ArrayList<>();
        for (DisplayResolveInfo appTarget : appTargets) {
            List<ShortcutManager.ShareShortcutInfo> matchingShortcuts =
                    filterShortcutsByTargetComponentName(
                            shortcuts, appTarget.getResolvedComponentName());
            if (!matchingShortcuts.isEmpty()) {
                List<ChooserTarget> chooserTargets =
                        mShortcutToChooserTargetConverter.convertToChooserTarget(
                                matchingShortcuts,
                                shortcuts,
                                appPredictorTargets,
                                directShareAppTargetCache,
                                directShareShortcutInfoCache);
                resultRecords.add(new ShortcutResultInfo(appTarget, chooserTargets));
            }
        }
        postReport(
                new Result(
                        isFromAppPredictor,
                        appTargets,
                        resultRecords.toArray(new ShortcutResultInfo[0]),
                        directShareAppTargetCache,
                        directShareShortcutInfoCache));
    }

    private void postReport(Result result) {
        mCallbackExecutor.execute(
                () -> {
                    Consumer<Result> callback = mCallback.get();
                    if (callback != null) {
                        callback.accept(result);
                    }
                });
    }

    private boolean shouldQueryDirectShareTargets() {
        return mIsPersonalProfile || isProfileActive();
    }

    protected boolean isProfileActive() {
        return mUserManager != null
                && mUserManager.isUserRunning(mUserHandle)
                && mUserManager.isUserUnlocked(mUserHandle)
                && !mUserManager.isQuietModeEnabled(mUserHandle);
    }

    private static boolean isPackageEnabled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo appInfo =
                    context.getPackageManager()
                            .getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(128L));
            return appInfo != null && appInfo.enabled && (appInfo.flags & 0x40000000) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static List<ShortcutManager.ShareShortcutInfo> filterShortcutsByTargetComponentName(
            List<ShortcutManager.ShareShortcutInfo> allShortcuts, ComponentName requiredTarget) {
        List<ShortcutManager.ShareShortcutInfo> matchingShortcuts = new ArrayList<>();
        for (ShortcutManager.ShareShortcutInfo shortcut : allShortcuts) {
            if (requiredTarget.equals(shortcut.getTargetComponent())) {
                matchingShortcuts.add(shortcut);
            }
        }
        return matchingShortcuts;
    }

    private static class Request {
        private final DisplayResolveInfo[] appTargets;

        Request(DisplayResolveInfo[] targets) {
            appTargets = targets;
        }
    }

    public static class Result {
        public final DisplayResolveInfo[] appTargets;
        public final Map<ChooserTarget, AppTarget> directShareAppTargetCache;
        public final Map<ChooserTarget, ShortcutInfo> directShareShortcutInfoCache;
        public final boolean isFromAppPredictor;
        public final ShortcutResultInfo[] shortcutsByApp;

        public Result(
                boolean isFromAppPredictor,
                DisplayResolveInfo[] appTargets,
                ShortcutResultInfo[] shortcutsByApp,
                Map<ChooserTarget, AppTarget> directShareAppTargetCache,
                Map<ChooserTarget, ShortcutInfo> directShareShortcutInfoCache) {
            this.isFromAppPredictor = isFromAppPredictor;
            this.appTargets = appTargets;
            this.shortcutsByApp = shortcutsByApp;
            this.directShareAppTargetCache = directShareAppTargetCache;
            this.directShareShortcutInfoCache = directShareShortcutInfoCache;
        }
    }

    public static class ShortcutResultInfo {
        public final DisplayResolveInfo appTarget;
        public final List<ChooserTarget> shortcuts;

        public ShortcutResultInfo(DisplayResolveInfo appTarget, List<ChooserTarget> shortcuts) {
            this.appTarget = appTarget;
            this.shortcuts = shortcuts;
        }
    }
}
