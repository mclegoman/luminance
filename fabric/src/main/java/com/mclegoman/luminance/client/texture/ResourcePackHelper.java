/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ResourcePackHelper {
	public static void register(Identifier id, ModContainer container, Text text, ActivationType activationType) {
		try {
			ResourcePackActivationType resourcePackActivationType = switch (activationType) {
				case disabledDefault -> ResourcePackActivationType.NORMAL;
				case enabledAlways -> ResourcePackActivationType.ALWAYS_ENABLED;
				case enabledDefault -> ResourcePackActivationType.DEFAULT_ENABLED;
			};
			Data.getVersion().sendToLog(LogType.INFO, Translation.getString("Registering resource pack: {}", id.getPath()));
			ResourceManagerHelper.registerBuiltinResourcePack(id, container, text, resourcePackActivationType);
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to register resource pack: {}", error));
		}
	}
}