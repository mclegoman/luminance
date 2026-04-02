/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ResourcePackHelper {
	public static void register(Identifier id, ModContainer container, Text text, PackActivationType packActivationType) {
		try {
			Data.getVersion().sendToLog(LogType.INFO, Translation.getString("Registering resource pack: {}", id.getPath()));
			FabricLoader.getInstance().getModContainer(container.getMetadata().getId()).ifPresent(modContainer -> ResourceLoader.registerBuiltinPack(id, modContainer, text, packActivationType));
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to register resource pack: {}", error));
		}
	}
}