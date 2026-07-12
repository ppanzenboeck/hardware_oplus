package com.oplus.shortcuts.chooser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import java.util.ArrayList;
import java.util.List;

public class DisplayResolveInfo implements TargetInfo {
    private static final String EXTRA_IS_FROM_CHOOSER = "oplus.intent.extra.from_chooser";

    private final IconHolder mDisplayIconHolder;
    private volatile CharSequence mDisplayLabel;
    private volatile CharSequence mExtendedInfo;
    private final boolean mIsSuspended;
    private boolean mPinned;
    private final ResolveInfo mResolveInfo;
    private final Intent mResolvedIntent;
    private final List<Intent> mSourceIntents;

    public static DisplayResolveInfo newDisplayResolveInfo(
            Intent originalIntent, ResolveInfo resolveInfo, Intent resolvedIntent) {
        return newDisplayResolveInfo(originalIntent, resolveInfo, null, null, resolvedIntent);
    }

    public static DisplayResolveInfo newDisplayResolveInfo(
            Intent originalIntent,
            ResolveInfo resolveInfo,
            CharSequence displayLabel,
            CharSequence extendedInfo,
            Intent resolvedIntent) {
        return new DisplayResolveInfo(originalIntent, resolveInfo, displayLabel, extendedInfo, resolvedIntent);
    }

    private DisplayResolveInfo(
            Intent originalIntent,
            ResolveInfo resolveInfo,
            CharSequence displayLabel,
            CharSequence extendedInfo,
            Intent resolvedIntent) {
        mSourceIntents = new ArrayList<>();
        mDisplayIconHolder = new TargetInfo.SettableIconHolder();
        mSourceIntents.add(originalIntent);
        mResolveInfo = resolveInfo;
        mDisplayLabel = displayLabel;
        mExtendedInfo = extendedInfo;
        ActivityInfo ai = mResolveInfo.activityInfo;
        mIsSuspended = (ai.applicationInfo.flags & 0x40000000) != 0;
        mResolvedIntent = createResolvedIntent(resolvedIntent, ai);
    }

    private DisplayResolveInfo(DisplayResolveInfo other, Intent baseIntentToSend) {
        mSourceIntents = new ArrayList<>(other.getAllSourceIntents());
        mDisplayIconHolder = new TargetInfo.SettableIconHolder();
        mResolveInfo = other.mResolveInfo;
        mIsSuspended = other.mIsSuspended;
        mDisplayLabel = other.mDisplayLabel;
        mExtendedInfo = other.mExtendedInfo;
        mResolvedIntent =
                createResolvedIntent(
                        baseIntentToSend == null ? other.mResolvedIntent : baseIntentToSend,
                        mResolveInfo.activityInfo);
        mDisplayIconHolder.setDisplayIcon(other.mDisplayIconHolder.getDisplayIcon());
    }

    private DisplayResolveInfo(DisplayResolveInfo other) {
        mSourceIntents = new ArrayList<>(other.getAllSourceIntents());
        mDisplayIconHolder = new TargetInfo.SettableIconHolder();
        mResolveInfo = other.mResolveInfo;
        mIsSuspended = other.mIsSuspended;
        mDisplayLabel = other.mDisplayLabel;
        mExtendedInfo = other.mExtendedInfo;
        mResolvedIntent = other.mResolvedIntent;
        mDisplayIconHolder.setDisplayIcon(other.mDisplayIconHolder.getDisplayIcon());
    }

    private static Intent createResolvedIntent(Intent resolvedIntent, ActivityInfo ai) {
        Intent result = new Intent(resolvedIntent);
        result.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        result.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));
        return result;
    }

    @Override
    public final boolean isDisplayResolveInfo() {
        return true;
    }

    @Override
    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

    @Override
    public CharSequence getDisplayLabel() {
        return mDisplayLabel;
    }

    public boolean hasDisplayLabel() {
        return mDisplayLabel != null;
    }

    public void setDisplayLabel(CharSequence displayLabel) {
        mDisplayLabel = displayLabel;
    }

    public void setExtendedInfo(CharSequence extendedInfo) {
        mExtendedInfo = extendedInfo;
    }

    @Override
    public IconHolder getDisplayIconHolder() {
        return mDisplayIconHolder;
    }

    @Override
    public DisplayResolveInfo tryToCloneWithAppliedRefinement(Intent proposedRefinement) {
        Intent matchingBase =
                getAllSourceIntents().stream()
                        .filter(intent -> intent.filterEquals(proposedRefinement))
                        .findFirst()
                        .orElse(null);
        if (matchingBase == null) {
            return null;
        }
        return new DisplayResolveInfo(
                this, TargetInfo.mergeRefinementIntoMatchingBaseIntent(matchingBase, proposedRefinement));
    }

    @Override
    public List<Intent> getAllSourceIntents() {
        return mSourceIntents;
    }

    @Override
    public ArrayList<DisplayResolveInfo> getAllDisplayTargets() {
        ArrayList<DisplayResolveInfo> targets = new ArrayList<>(1);
        targets.add(this);
        return targets;
    }

    public void addAlternateSourceIntent(Intent alt) {
        mSourceIntents.add(alt);
    }

    @Override
    public CharSequence getExtendedInfo() {
        return mExtendedInfo;
    }

    @Override
    public Intent getResolvedIntent() {
        return mResolvedIntent;
    }

    @Override
    public ComponentName getResolvedComponentName() {
        return new ComponentName(mResolveInfo.activityInfo.packageName, mResolveInfo.activityInfo.name);
    }

    @Override
    public boolean startAsCaller(Activity activity, Bundle options, int userId) {
        TargetInfo.prepareIntentForCrossProfileLaunch(activity, mResolvedIntent, userId);
        mResolvedIntent.putExtra(EXTRA_IS_FROM_CHOOSER, true);
        TargetInfo.refreshIntentCreatorToken(mResolvedIntent);
        activity.startActivityAsCaller(mResolvedIntent, options, false, userId);
        return true;
    }

    @Override
    public boolean startAsUser(Activity activity, Bundle options, UserHandle user) {
        TargetInfo.prepareIntentForCrossProfileLaunch(activity, mResolvedIntent, user.getIdentifier());
        mResolvedIntent.putExtra(EXTRA_IS_FROM_CHOOSER, true);
        TargetInfo.refreshIntentCreatorToken(mResolvedIntent);
        activity.startActivityAsUser(mResolvedIntent, options, user);
        return false;
    }

    @Override
    public Intent getTargetIntent() {
        return mResolvedIntent;
    }

    @Override
    public boolean isSuspended() {
        return mIsSuspended;
    }

    @Override
    public boolean isPinned() {
        return mPinned;
    }

    public void setPinned(boolean pinned) {
        mPinned = pinned;
    }

    public DisplayResolveInfo copy() {
        return new DisplayResolveInfo(this);
    }
}
