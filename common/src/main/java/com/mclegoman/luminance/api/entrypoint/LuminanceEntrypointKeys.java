/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.api.entrypoint;

public class LuminanceEntrypointKeys {
	// TODO: Pre-Launch Initializer.
	public static final String clientInitKey;
	public static final String commonInitKey;
	public static final String serverInitKey;
	static {
		clientInitKey = "luminance_client";
		commonInitKey = "luminance_common";
		serverInitKey = "luminance_server";
	}
}