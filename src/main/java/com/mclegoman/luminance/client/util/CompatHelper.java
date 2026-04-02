/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.Couple;
import com.mclegoman.luminance.common.util.DateHelper;
import net.irisshaders.iris.api.v0.IrisApi;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CompatHelper {
	public static final Map<Couple<String, String>, IconOverride> iconOverrides = new HashMap<>();
	public static final List<String> luminanceModMenuBadge = new ArrayList<>();
	public static void init() {
		addIconOverride(new Couple<>(Data.getVersion().getID(), "pride"), new IconOverride(Data.idOf("textures/icons/pride.png"), DateHelper::isPride));
		addLuminanceModMenuBadge(Data.getVersion().getID());
	}
	public static boolean isIrisShadersEnabled() {
		try {
			return Data.isModInstalled("iris") && IrisApi.getInstance().isShaderPackInUse();
		} catch (Exception ignored) {
			// We use a try/catch here just in case Iris ever changes where isShaderPackInUse is located.
			return false;
		}
	}
	public static void addIconOverride(Couple<String, String> modId, IconOverride iconOverride) {
		if (!shouldIconOverride(modId.getFirst())) iconOverrides.put(modId, iconOverride);
	}
	public static void replaceIconOverride(Couple<String, String> modId, IconOverride iconOverride) {
		iconOverrides.replace(modId, iconOverride);
	}
	public static void removeIconOverride(Couple<String, String> modId) {
		iconOverrides.remove(modId);
	}
	public static boolean shouldIconOverride(String modId) {
		AtomicReference<Boolean> shouldOverride = new AtomicReference<>(false);
		iconOverrides.forEach((mod, data) -> {
			if (mod.getFirst().equalsIgnoreCase(modId)) {
				try {shouldOverride.set(data.shouldOverride());
				} catch (Exception ignored) {}
			}
		});
		return shouldOverride.get();
	}
	public static IconOverride getIconOverride(String modId) {
		return getIconOverride(modId, null);
	}
	public static IconOverride getIconOverride(String modId, IconOverride fallback) {
		AtomicReference<IconOverride> icon = new AtomicReference<>(fallback);
		iconOverrides.forEach((mod, data) -> {
			if (mod.getFirst().equalsIgnoreCase(modId)) {
				try {
					if (data.shouldOverride()) {
						icon.set(data);
					}
				} catch (Exception ignored) {}
			}
		});
		return icon.get();
	}
	public static void addLuminanceModMenuBadge(String modId) {
		if (!getLuminanceModMenuBadge(modId)) luminanceModMenuBadge.add(modId);
	}
	public static boolean getLuminanceModMenuBadge(String modId) {
		return luminanceModMenuBadge.contains(modId);
	}
	public static void removeLuminanceModMenuBadge(String modId) {
		luminanceModMenuBadge.remove(modId);
	}
}
