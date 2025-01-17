/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ResourcePacks {
	/**
	 * To add a resource pack to this project, please follow these guidelines:
	 * 1. When registering your resource pack, ensure you include the resource pack's name, and the contributor(s) in the following format:
	 * - Resource Pack Name
	 * - Contributor(s): _________
	 * - Licence: _________
	 * - Notes: _________
	 * You only need to include the licence in your comment if it is not GNU LGPLv3.
	 */
	public static void init() {
		Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Data.getVersion().getID());
		if (modContainer.isPresent()) {
			/*
	            Super Secret Settings
	            Contributor(s): Mojang Studios, Microsoft Corporation, dannytaylor, Nettakrim
	            Licence: Minecraft EULA
	            Notes: These shaders have been modified to work with the latest version of minecraft, and also contain new code.
            */
			ResourcePackHelper.register(Identifier.of("super_secret_settings"), modContainer.get(), Translation.getTranslation(Data.getVersion().getID(), "resource_pack.super_secret_settings"), ActivationType.enabledDefault);
			/*
	            Luminance: Default
	            Contributor(s): dannytaylor
	            Licence: GNU LGPLv3
	        */
			ResourcePackHelper.register(Identifier.of("luminance_default"), modContainer.get(), Translation.getTranslation(Data.getVersion().getID(), "resource_pack.luminance_default"), ActivationType.enabledDefault);
		}
	}
}