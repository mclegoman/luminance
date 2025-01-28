/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.data;

import com.mclegoman.luminance.api.mod.ModContainer;
import com.mclegoman.luminance.api.mod.ModHelper;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.util.LogType;
import com.mclegoman.luminance.common.util.Version;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class Data {
	private static Version version;
	public static Version getVersion() {
		if (version == null) {
			Optional<ModContainer> modContainer = ModHelper.getModContainer("luminance");
			modContainer.ifPresent(container -> version = Version.parse(container.metadata(), "EBTw0O1c"));
		}
		if (version != null) return version;
		else throw new NullPointerException("luminance version couldn't be obtained!");
		// If you end up seeing this error in dev env, try reloading your gradle.
	}
	public static boolean isModInstalled(String modId) {
		try {
			return ModHelper.isModLoaded(modId);
		} catch (Exception ignored) {
			return false;
		}
	}
	public static boolean isModInstalledVersionOrHigher(String modId, String requiredVersion, boolean substring, String separator) {
		try {
			if (isModInstalled(modId)) {
				Optional<ModContainer> modContainer = ModHelper.getModContainer(modId);
				if (modContainer.isPresent()) return checkModVersion(modContainer.get().metadata().rawVersion(), requiredVersion, substring);
			}
		} catch (Exception error) {
			version.sendToLog(LogType.ERROR, Translation.getString("Failed to check mod version for " + modId + ": {}", error));
		}
		return false;
	}
	public static boolean isModInstalledVersionOrHigher(String modId, String requiredVersion, boolean substring) {
		return isModInstalledVersionOrHigher(modId, requiredVersion, substring, "-");
	}
	public static boolean isModInstalledVersionOrHigher(String modId, String requiredVersion) {
		return isModInstalledVersionOrHigher(modId, requiredVersion, false);
	}
	public static boolean checkModVersion(String currentVersion, String requiredVersion, boolean substring, String separator) {
		try {
			return net.fabricmc.loader.api.Version.parse(requiredVersion).compareTo(net.fabricmc.loader.api.Version.parse(substring ? StringUtils.substringBefore(currentVersion, separator) : currentVersion)) <= 0;
		} catch (Exception error) {
			version.sendToLog(LogType.ERROR, Translation.getString("Failed to check mod version!"));
		}
		return false;
	}
	public static boolean checkModVersion(String currentVersion, String requiredVersion, boolean substring) {
		return checkModVersion(currentVersion, requiredVersion, substring, "-");
	}
}