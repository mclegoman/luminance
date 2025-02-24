/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.texture.ResourcePacks;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.client.util.MessageOverlay;
import com.mclegoman.luminance.client.util.Tick;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;

public class LuminanceClient {
	public static void init(String modId) {
		try {
			Data.getVersion().sendToLog(LogType.INFO, Translation.getString("Initializing {}:client", Data.getVersion().getName()));
			LuminanceConfig.init();
			ResourcePacks.init();
			Keybindings.init();
			CompatHelper.init();
			Shaders.init();
			MessageOverlay.init();
			Tick.init();
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to run client:init: {}", error));
		}
	}
}