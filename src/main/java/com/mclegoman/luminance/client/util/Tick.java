/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Tick {
	public static void init() {
		try {
			ClientTickEvents.END_CLIENT_TICK.register((client) -> {
				if (ClientData.minecraft.isFinishedLoading()) {
					Keybindings.tick();
					MessageOverlay.tick();
					Uniforms.tick();
				}
			});
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to initialize tick: {}", error));
		}
	}
}
