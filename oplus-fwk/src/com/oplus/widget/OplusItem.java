package com.oplus.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class OplusItem {
    public static final int ITEM_FIRST = 0;
    public static final int ITEM_SECOND = 1;
    public static final int ITEM_THIRD = 2;
    public static final int ITEM_FOURTH = 3;
    public static final int ITEM_FIFTH = 4;

    private Drawable mBackgroud;
    private Context mContext;
    private Drawable mIcon;
    private String mLabel;
    private OnItemClickListener mOnItemClickListener;
    private String mPackageName;
    private boolean mPinned;
    private String mText;

    public interface OnItemClickListener {
        void OnMenuItemClick(int position);
    }

    public static class Builder {
        private final OplusItem mItem = new OplusItem();

        public Builder(Context context) {
            mItem.mContext = context;
        }

        public Builder setText(String text) {
            mItem.mText = text;
            return this;
        }

        public Builder setText(int textResId) {
            mItem.mText = mItem.getContext().getString(textResId);
            return this;
        }

        public Builder setLabel(String label) {
            mItem.mLabel = label;
            return this;
        }

        public Builder setLabel(int labelId) {
            mItem.mLabel = mItem.getContext().getString(labelId);
            return this;
        }

        public Builder setIcon(Drawable icon) {
            mItem.mIcon = icon;
            return this;
        }

        public Builder setIcon(int iconResId) {
            mItem.mIcon = mItem.getContext().getResources().getDrawable(iconResId);
            return this;
        }

        public Builder setBackgroud(Drawable background) {
            mItem.mBackgroud = background;
            return this;
        }

        public Builder setBackgroud(int bgResId) {
            mItem.mBackgroud = mItem.getContext().getResources().getDrawable(bgResId);
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener listener) {
            mItem.mOnItemClickListener = listener;
            return this;
        }

        public Builder setPackageName(String packageName) {
            mItem.mPackageName = packageName;
            return this;
        }

        public OplusItem create() {
            return mItem;
        }
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public Drawable getBackgroud() {
        return mBackgroud;
    }

    public void setBackgroud(Drawable backgroud) {
        mBackgroud = backgroud;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public boolean isPinned() {
        return mPinned;
    }

    public void setPinned(Boolean pinned) {
        mPinned = pinned != null && pinned;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }
}
