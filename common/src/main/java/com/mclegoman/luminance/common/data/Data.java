/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.data;

import com.mclegoman.luminance.common.util.ReleaseType;
import com.mclegoman.luminance.common.util.Version;

public class Data {
	// This should get overridden by the modLoader, so we don't have to have this set.
	public static final Version version = Version.create("Luminance", "luminance", 0, 0, 0, ReleaseType.RELEASE, 0);
	public static boolean isModInstalled(String modId) {
		return false;
	}
	public static boolean isModInstalledVersionOrHigher(String modId, String requiredVersion, boolean substring, String separator) {
		return false;
	}
	public static boolean isModInstalledVersionOrHigher(String modId, String requiredVersion, boolean substring) {
		return isModInstalledVersionOrHigher(modId, requiredVersion, substring, "-");
	}
	public static boolean isModInstalledVersionOrHigher(String modId, String requiredVersion) {
		return isModInstalledVersionOrHigher(modId, requiredVersion, false);
	}
	public static boolean checkModVersion(String currentVersion, String requiredVersion, boolean substring, String separator) {
		return false;
	}
	public static boolean checkModVersion(String currentVersion, String requiredVersion, boolean substring) {
		return checkModVersion(currentVersion, requiredVersion, substring, "-");
	}
}