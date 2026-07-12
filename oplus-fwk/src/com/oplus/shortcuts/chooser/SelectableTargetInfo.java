package com.oplus.shortcuts.chooser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import com.oplus.wrapper.app.prediction.AppTarget;
import java.util.ArrayList;
import java.util.List;

public final class SelectableTargetInfo extends ChooserTargetInfo {
    private static final String TAG = "SelectableTargetInfo";

    private final List<Intent> mAllSourceIntents;
    private final AppTarget mAppTarget;
    private final ResolveInfo mBackupResolveInfo;
    private final Intent mBaseIntentToSend;
    private final ComponentName mChooserTargetComponentName;
    private final Icon mChooserTargetIcon;
    private final Bundle mChooserTargetIntentExtras;
    private final CharSequence mChooserTargetUnsanitizedTitle;
    private final IconHolder mDisplayIconHolder;
    private final String mDisplayLabel;
    private final boolean mIsPinned;
    private final boolean mIsSuspended;
    private final float mModifiedScore;
    private final Intent mReferrerFillInIntent;
    private final ResolveInfo mResolveInfo;
    private final ComponentName mResolvedComponentName;
    private final Intent mResolvedIntent;
    private final ShortcutInfo mShortcutInfo;
    private final DisplayResolveInfo mSourceInfo;

    public static TargetInfo newSelectableTargetInfo(
            DisplayResolveInfo sourceInfo,
            ResolveInfo backupResolveInfo,
            Intent resolvedIntent,
            ComponentName chooserTargetComponentName,
            CharSequence chooserTargetUnsanitizedTitle,
            Icon chooserTargetIcon,
            Bundle chooserTargetIntentExtras,
            float modifiedScore,
            ShortcutInfo shortcutInfo,
            AppTarget appTarget,
            Intent referrerFillInIntent) {
        return new SelectableTargetInfo(
                sourceInfo,
                backupResolveInfo,
                resolvedIntent,
                null,
                chooserTargetComponentName,
                chooserTargetUnsanitizedTitle,
                chooserTargetIcon,
                chooserTargetIntentExtras,
                modifiedScore,
                shortcutInfo,
                appTarget,
                referrerFillInIntent);
    }

    private SelectableTargetInfo(
            DisplayResolveInfo sourceInfo,
            ResolveInfo backupResolveInfo,
            Intent resolvedIntent,
            Intent baseIntentToSend,
            ComponentName chooserTargetComponentName,
            CharSequence chooserTargetUnsanitizedTitle,
            Icon chooserTargetIcon,
            Bundle chooserTargetIntentExtras,
            float modifiedScore,
            ShortcutInfo shortcutInfo,
            AppTarget appTarget,
            Intent referrerFillInIntent) {
        mDisplayIconHolder = new TargetInfo.SettableIconHolder();
        mSourceInfo = sourceInfo;
        mBackupResolveInfo = backupResolveInfo;
        mResolvedIntent = resolvedIntent;
        mModifiedScore = modifiedScore;
        mShortcutInfo = shortcutInfo;
        mAppTarget = appTarget;
        mReferrerFillInIntent = referrerFillInIntent;
        mChooserTargetComponentName = chooserTargetComponentName;
        mChooserTargetUnsanitizedTitle = chooserTargetUnsanitizedTitle;
        mChooserTargetIcon = chooserTargetIcon;
        mChooserTargetIntentExtras = chooserTargetIntentExtras == null ? new Bundle() : chooserTargetIntentExtras;
        mIsPinned = shortcutInfo != null && shortcutInfo.isPinned();
        mDisplayLabel = sanitizeDisplayLabel(mChooserTargetUnsanitizedTitle);
        mIsSuspended = mSourceInfo != null && mSourceInfo.isSuspended();
        mResolveInfo = mSourceInfo != null ? mSourceInfo.getResolveInfo() : mBackupResolveInfo;
        mResolvedComponentName = getResolvedComponentName(mSourceInfo, mBackupResolveInfo);
        mBaseIntentToSend = getBaseIntentToSend(baseIntentToSend, mResolvedIntent, mReferrerFillInIntent);
        mAllSourceIntents = getAllSourceIntents(sourceInfo, mBaseIntentToSend);
    }

    private SelectableTargetInfo(SelectableTargetInfo other, Intent baseIntentToSend) {
        this(
                other.mSourceInfo,
                other.mBackupResolveInfo,
                other.mResolvedIntent,
                baseIntentToSend,
                other.mChooserTargetComponentName,
                other.mChooserTargetUnsanitizedTitle,
                other.mChooserTargetIcon,
                other.mChooserTargetIntentExtras,
                other.mModifiedScore,
                other.mShortcutInfo,
                other.mAppTarget,
                other.mReferrerFillInIntent);
    }

    @Override
    public TargetInfo tryToCloneWithAppliedRefinement(Intent proposedRefinement) {
        Intent matchingBase =
                getAllSourceIntents().stream()
                        .filter(intent -> intent.filterEquals(proposedRefinement))
                        .findFirst()
                        .orElse(null);
        if (matchingBase == null) {
            return null;
        }
        return new SelectableTargetInfo(
                this, TargetInfo.mergeRefinementIntoMatchingBaseIntent(matchingBase, proposedRefinement));
    }

    @Override
    public boolean isSelectableTargetInfo() {
        return true;
    }

    @Override
    public boolean isSuspended() {
        return mIsSuspended;
    }

    @Override
    public DisplayResolveInfo getDisplayResolveInfo() {
        return mSourceInfo;
    }

    @Override
    public float getModifiedScore() {
        return mModifiedScore;
    }

    @Override
    public Intent getResolvedIntent() {
        return mResolvedIntent;
    }

    @Override
    public ComponentName getResolvedComponentName() {
        return mResolvedComponentName;
    }

    @Override
    public ComponentName getChooserTargetComponentName() {
        return mChooserTargetComponentName;
    }

    public Icon getChooserTargetIcon() {
        return mChooserTargetIcon;
    }

    @Override
    public boolean startAsCaller(Activity activity, Bundle options, int userId) {
        Intent intent = mBaseIntentToSend;
        if (intent == null) {
            return false;
        }
        intent.setComponent(getChooserTargetComponentName());
        intent.putExtras(mChooserTargetIntentExtras);
        TargetInfo.prepareIntentForCrossProfileLaunch(activity, intent, userId);
        TargetInfo.refreshIntentCreatorToken(intent);
        boolean ignoreTargetSecurity =
                mSourceInfo != null
                        && mSourceInfo.getResolvedComponentName()
                                .getPackageName()
                                .equals(getChooserTargetComponentName().getPackageName());
        activity.startActivityAsCaller(intent, options, ignoreTargetSecurity, userId);
        return true;
    }

    @Override
    public boolean startAsUser(Activity activity, Bundle options, UserHandle user) {
        throw new RuntimeException("ChooserTargets should be started as caller.");
    }

    @Override
    public Intent getTargetIntent() {
        return mBaseIntentToSend;
    }

    @Override
    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

    @Override
    public CharSequence getDisplayLabel() {
        return mDisplayLabel;
    }

    @Override
    public CharSequence getExtendedInfo() {
        return null;
    }

    @Override
    public IconHolder getDisplayIconHolder() {
        return mDisplayIconHolder;
    }

    @Override
    public ShortcutInfo getDirectShareShortcutInfo() {
        return mShortcutInfo;
    }

    @Override
    public AppTarget getDirectShareAppTarget() {
        return mAppTarget;
    }

    @Override
    public List<Intent> getAllSourceIntents() {
        return mAllSourceIntents;
    }

    @Override
    public boolean isPinned() {
        return mIsPinned;
    }

    public Bundle getChooserTargetIntentExtras() {
        return mChooserTargetIntentExtras;
    }

    private static String sanitizeDisplayLabel(CharSequence label) {
        if (label == null) {
            return "";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(label);
        builder.clearSpans();
        return builder.toString();
    }

    private static List<Intent> getAllSourceIntents(
            DisplayResolveInfo sourceInfo, Intent fallbackSourceIntent) {
        List<Intent> results = new ArrayList<>();
        if (sourceInfo != null) {
            results.addAll(sourceInfo.getAllSourceIntents());
        } else if (fallbackSourceIntent != null) {
            results.add(fallbackSourceIntent);
        }
        return results;
    }

    private static ComponentName getResolvedComponentName(
            DisplayResolveInfo sourceInfo, ResolveInfo backupResolveInfo) {
        if (sourceInfo != null) {
            return sourceInfo.getResolvedComponentName();
        }
        if (backupResolveInfo != null) {
            return new ComponentName(backupResolveInfo.activityInfo.packageName, backupResolveInfo.activityInfo.name);
        }
        return null;
    }

    private static Intent getBaseIntentToSend(
            Intent providedBase, Intent fallbackBase, Intent referrerFillInIntent) {
        Intent result = providedBase != null ? providedBase : fallbackBase;
        if (result == null) {
            Log.e(TAG, "ChooserTargetInfo: no base intent available to send");
            return null;
        }
        Intent copy = new Intent(result);
        if (referrerFillInIntent != null) {
            copy.fillIn(referrerFillInIntent, 0);
        }
        return copy;
    }
}
