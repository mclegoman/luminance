/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.data;

import com.mclegoman.luminance.client.LuminanceClient;

public class Data {
	public static boolean isDevelopmentBuild() {
		String version = LuminanceClient.getMod().getMetadata().getVersion().getFriendlyString();
		return version.contains("alpha") || version.contains("beta") || version.contains("rc");
	}

	public static String getFormattedVersion() {
		String version = LuminanceClient.getMod().getMetadata().getVersion().getFriendlyString();
		return version.substring(0, version.indexOf("+"));
	}
}