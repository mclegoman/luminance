/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.api.mod;

import java.util.Optional;

public class ModHelper {
	public static Optional<ModContainer> getModContainer(String modId) {
		// We shouldn't have this function be used as it gets overridden by the modloader subprojects.
		System.out.println("Unexpectedly used :common for ModHelper.getModContainer();!");
		return Optional.empty();
	}
	public static boolean isModLoaded(String modId) {
		return false;
	}
}
