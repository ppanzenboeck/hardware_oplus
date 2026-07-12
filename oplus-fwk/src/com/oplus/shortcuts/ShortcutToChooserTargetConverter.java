package com.oplus.shortcuts;

import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import com.oplus.wrapper.app.prediction.AppTarget;
import com.oplus.wrapper.content.pm.ShortcutManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

class ShortcutToChooserTargetConverter {
    List<ChooserTarget> convertToChooserTarget(
            List<ShortcutManager.ShareShortcutInfo> matchingShortcuts,
            List<ShortcutManager.ShareShortcutInfo> allShortcuts,
            List<AppTarget> allAppTargets,
            Map<ChooserTarget, AppTarget> directShareAppTargetCache,
            Map<ChooserTarget, ShortcutInfo> directShareShortcutInfoCache) {
        boolean fromAppPredictor = allAppTargets != null;
        List<Integer> ranks = new ArrayList<>();
        if (!fromAppPredictor) {
            for (ShortcutManager.ShareShortcutInfo shortcut : matchingShortcuts) {
                int rank = shortcut.getShortcutInfo().getRank();
                if (!ranks.contains(rank)) {
                    ranks.add(rank);
                }
            }
            Collections.sort(ranks);
        }

        List<ChooserTarget> targets = new ArrayList<>(matchingShortcuts.size());
        for (ShortcutManager.ShareShortcutInfo shortcut : matchingShortcuts) {
            ShortcutInfo shortcutInfo = shortcut.getShortcutInfo();
            int indexInAllShortcuts = allShortcuts.indexOf(shortcut);
            float score;
            if (fromAppPredictor) {
                score = Math.max(1.0f - (indexInAllShortcuts * 0.01f), 0.0f);
            } else {
                score = Math.max(1.0f - (ranks.indexOf(shortcutInfo.getRank()) * 0.01f), 0.0f);
            }

            Bundle extras = new Bundle();
            extras.putString("android.intent.extra.shortcut.ID", shortcutInfo.getId());
            ChooserTarget target =
                    new ChooserTarget(
                            shortcutInfo.getLabel(),
                            null,
                            score,
                            shortcut.getTargetComponent().clone(),
                            extras);
            targets.add(target);
            if (directShareAppTargetCache != null && allAppTargets != null && indexInAllShortcuts >= 0) {
                directShareAppTargetCache.put(target, allAppTargets.get(indexInAllShortcuts));
            }
            if (directShareShortcutInfoCache != null) {
                directShareShortcutInfoCache.put(target, shortcutInfo);
            }
        }
        targets.sort(Comparator.comparing(ChooserTarget::getScore).reversed());
        return targets;
    }
}
