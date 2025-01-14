/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.Couple;
import com.mclegoman.luminance.common.util.DateHelper;
import net.irisshaders.iris.api.v0.IrisApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class CompatHelper {
	public static final Map<Couple<String, String>, Couple<Callable<String>, Callable<Boolean>>> overriddenModMenuIcons = new HashMap<>();
	public static final List<String> luminanceModMenuBadge = new ArrayList<>();
	public static void init() {
		addOverrideModMenuIcon(new Couple<>(Data.version.getID(), "pride"), () -> "assets/" + Data.version.getID() + "/icons/pride.png", DateHelper::isPride);
		addLuminanceModMenuBadge(Data.version.getID());
	}
	public static boolean isIrisShadersEnabled() {
		try {
			return Data.isModInstalled("iris") && IrisApi.getInstance().isShaderPackInUse();
		} catch (Exception ignored) {
			// We use a try/catch here just in case Iris ever changes where isShaderPackInUse is located.
			return false;
		}
	}
	public static void addOverrideModMenuIcon(Couple<String, String> modId, Callable<String> iconPath, Callable<Boolean> shouldOverride) {
		if (!shouldOverrideModMenuIcon(modId.getFirst())) overriddenModMenuIcons.put(modId, new Couple<>(iconPath, shouldOverride));
	}
	public static void modifyOverrideModMenuIcon(Couple<String, String> modId, Callable<String> iconPath, Callable<Boolean> shouldOverride) {
		overriddenModMenuIcons.replace(modId, new Couple<>(iconPath, shouldOverride));
	}
	public static void removeOverrideModMenuIcon(Couple<String, String> modId) {
		overriddenModMenuIcons.remove(modId);
	}
	public static boolean shouldOverrideModMenuIcon(String modId) {
		AtomicReference<Boolean> shouldOverride = new AtomicReference<>(false);
		overriddenModMenuIcons.forEach((mod, data) -> {
			if (mod.getFirst().equalsIgnoreCase(modId)) {
				try {shouldOverride.set(data.getSecond().call());
				} catch (Exception ignored) {}
			}
		});
		return shouldOverride.get();
	}
	public static String getOverrideModMenuIcon(String modId) {
		AtomicReference<String> modMenuIcon = new AtomicReference<>(null);
		overriddenModMenuIcons.forEach((mod, data) -> {
			if (mod.getFirst().equalsIgnoreCase(modId)) {
				try {
					if (data.getSecond().call()) {
						modMenuIcon.set(data.getFirst().call());
					}
				} catch (Exception ignored) {}
			}
		});
		return modMenuIcon.get();
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
