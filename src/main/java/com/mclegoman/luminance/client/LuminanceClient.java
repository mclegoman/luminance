/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client;

import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.texture.TextureHelper;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.client.util.MessageOverlay;
import com.mclegoman.luminance.client.util.Tick;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mclegoman.luminance.entrypoint.LuminanceInit;
import net.fabricmc.loader.api.ModContainer;

public class LuminanceClient implements LuminanceInit {
	public void init(ModContainer mod) {
		try {
			Data.version.sendToLog(LogType.INFO, Translation.getString("Initializing {}", Data.version.getName()));
			Keybindings.init();
			TextureHelper.init();
			CompatHelper.init();
			Shaders.init();
			MessageOverlay.init();
			Tick.init();
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to run onInitializeClient: {}", error));
		}
	}
}