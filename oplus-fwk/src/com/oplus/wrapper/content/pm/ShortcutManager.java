package com.oplus.wrapper.content.pm;

import android.content.ComponentName;
import android.content.pm.ShortcutInfo;

public class ShortcutManager {
    private ShortcutManager() {
    }

    public static class ShareShortcutInfo {
        private final android.content.pm.ShortcutManager.ShareShortcutInfo mShareShortcutInfo;

        public ShareShortcutInfo(android.content.pm.ShortcutManager.ShareShortcutInfo shareShortcutInfo) {
            mShareShortcutInfo = shareShortcutInfo;
        }

        public ShortcutInfo getShortcutInfo() {
            return mShareShortcutInfo.getShortcutInfo();
        }

        public ComponentName getTargetComponent() {
            return mShareShortcutInfo.getTargetComponent();
        }
    }
}
