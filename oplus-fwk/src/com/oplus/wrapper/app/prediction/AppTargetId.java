package com.oplus.wrapper.app.prediction;

public class AppTargetId {
    private final android.app.prediction.AppTargetId mAppTargetId;

    public AppTargetId(String id) {
        mAppTargetId = new android.app.prediction.AppTargetId(id);
    }

    public android.app.prediction.AppTargetId getAppTargetId() {
        return mAppTargetId;
    }
}
