/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import com.mclegoman.luminance.client.LuminanceClient;
import dev.dannytaylor.perspective.seam.common.data.log.SeamLog;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ResourcePackHelper {
	public static void register(Identifier id, ModContainer container, Component text, PackActivationType packActivationType) {
		try {
			SeamLog.info(LuminanceClient.getMod(), "Registering resource pack: {}", id.getPath());
			FabricLoader.getInstance().getModContainer(container.getMetadata().getId()).ifPresent(modContainer -> ResourceLoader.registerBuiltinPack(id, modContainer, text, packActivationType));
		} catch (Exception error) {
			SeamLog.error(LuminanceClient.getMod(), "Failed to register resource pack", error);
		}
	}
}