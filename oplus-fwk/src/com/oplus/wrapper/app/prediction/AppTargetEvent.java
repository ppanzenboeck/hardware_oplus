package com.oplus.wrapper.app.prediction;

import android.os.Parcelable;

public class AppTargetEvent {
    public static final int ACTION_LAUNCH = 1;
    public static final int ACTION_DISMISS = 2;
    public static final int ACTION_PIN = 3;
    public static final int ACTION_UNPIN = 4;
    public static final int ACTION_UNDISMISS = 5;

    private final android.app.prediction.AppTargetEvent mEvent;

    private AppTargetEvent(android.app.prediction.AppTargetEvent event) {
        mEvent = event;
    }

    public android.app.prediction.AppTargetEvent getAppTargetEvent() {
        return mEvent;
    }

    public int getAction() {
        return mEvent.getAction();
    }

    public String getLaunchLocation() {
        return mEvent.getLaunchLocation();
    }

    public Parcelable getParcelable() {
        return mEvent;
    }

    public static final class Builder {
        private final android.app.prediction.AppTargetEvent.Builder mAppTargetEventBuilder;

        public Builder(AppTarget target, int actionType) {
            mAppTargetEventBuilder =
                    new android.app.prediction.AppTargetEvent.Builder(
                            target.getAppTarget(), actionType);
        }

        public AppTargetEvent build() {
            return new AppTargetEvent(mAppTargetEventBuilder.build());
        }

        public Builder setLaunchLocation(String location) {
            mAppTargetEventBuilder.setLaunchLocation(location);
            return this;
        }
    }
}
