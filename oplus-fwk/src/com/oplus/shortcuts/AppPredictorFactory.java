package com.oplus.shortcuts;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import com.oplus.wrapper.app.prediction.AppPredictionContext;
import com.oplus.wrapper.app.prediction.AppPredictionManager;
import com.oplus.wrapper.app.prediction.AppPredictor;
import java.util.Objects;

public class AppPredictorFactory {
    private static final String APP_PREDICTION_INTENT_FILTER_KEY = "intent_filter";
    private static final String APP_PREDICTION_SHARE_UI_SURFACE = "share";
    private static final int APP_PREDICTION_SHARE_TARGET_QUERY_PACKAGE_LIMIT = 20;
    private static final String SHARED_TEXT_KEY = "shared_text";

    private final boolean mAppPredictionAvailable;
    private final Context mContext;
    private final String mSharedText;
    private final IntentFilter mTargetIntentFilter;

    public AppPredictorFactory(
            Context context,
            String sharedText,
            IntentFilter targetIntentFilter,
            boolean appPredictionAvailable) {
        mContext = Objects.requireNonNull(context, "context cannot be null");
        mSharedText = sharedText;
        mTargetIntentFilter = targetIntentFilter;
        mAppPredictionAvailable = appPredictionAvailable;
    }

    public AppPredictor create(UserHandle userHandle) {
        if (!mAppPredictionAvailable) {
            return null;
        }
        Context contextAsUser = mContext.createContextAsUser(userHandle, 0);
        Bundle extras = new Bundle();
        extras.putParcelable(APP_PREDICTION_INTENT_FILTER_KEY, mTargetIntentFilter);
        extras.putString(SHARED_TEXT_KEY, mSharedText);
        AppPredictionContext predictionContext =
                new AppPredictionContext.Builder(contextAsUser)
                        .setUiSurface(APP_PREDICTION_SHARE_UI_SURFACE)
                        .setPredictedTargetCount(APP_PREDICTION_SHARE_TARGET_QUERY_PACKAGE_LIMIT)
                        .setExtras(extras)
                        .build();
        return new AppPredictionManager(contextAsUser).createAppPredictionSession(predictionContext);
    }
}
