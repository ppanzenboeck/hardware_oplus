package com.oplus.resolver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import com.oplus.widget.OplusItem;

public class OplusGalleryLoadIconHelper {
    private static volatile OplusGalleryLoadIconHelper sLoadIconHelper;

    private final Context mContext;
    private UserHandle mUserHandle;

    public static OplusGalleryLoadIconHelper getInstance(Context context) {
        if (sLoadIconHelper == null) {
            synchronized (OplusGalleryLoadIconHelper.class) {
                if (sLoadIconHelper == null) {
                    sLoadIconHelper = new OplusGalleryLoadIconHelper(context.getApplicationContext());
                }
            }
        }
        return sLoadIconHelper;
    }

    private OplusGalleryLoadIconHelper(Context context) {
        mContext = context;
        mUserHandle = UserHandle.of(context.getUserId());
    }

    public Drawable loadUxIcon(Intent originIntent, ResolveInfo info) {
        if (info == null) {
            return null;
        }
        Drawable icon = info.loadIcon(mContext.getPackageManager());
        return icon != null ? mContext.getPackageManager().getUserBadgedIcon(icon, mUserHandle) : null;
    }

    public OplusItem getOplusItem(Intent originIntent, ResolveInfo info, PackageManager packageManager) {
        if (info == null || packageManager == null) {
            return null;
        }

        OplusItem item = new OplusItem();
        CharSequence label = info.loadLabel(packageManager);
        item.setText(label != null ? label.toString() : null);
        if (info.activityInfo != null) {
            item.setPackageName(info.activityInfo.packageName);
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;
            if (appInfo != null) {
                CharSequence appLabel = appInfo.loadLabel(packageManager);
                if (appLabel != null && !TextUtils.equals(item.getText(), appLabel)) {
                    item.setLabel(appLabel.toString());
                }
            }
        }

        Drawable icon = info.loadIcon(packageManager);
        if (icon != null) {
            item.setIcon(packageManager.getUserBadgedIcon(icon, mUserHandle));
        }
        return item;
    }

    public void updateUserHandle(UserHandle userHandle) {
        if (userHandle != null) {
            mUserHandle = userHandle;
        }
    }
}
