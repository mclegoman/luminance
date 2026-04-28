/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.texture.ResourcePacks;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.client.util.Tick;
import dev.dannytaylor.perspective.seam.common.data.FabricMod;
import dev.dannytaylor.perspective.seam.common.events.SeamEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class LuminanceClient implements ClientModInitializer {
	private static final FabricMod mod = FabricMod.fromMetadata(getModContainer().getMetadata());

	public static FabricMod getMod() {
		return mod;
	}

	public static ModContainer getModContainer() {
		return FabricLoader.getInstance().getModContainer("luminance").orElseThrow();
	}

	public void onInitializeClient() {
		SeamEvents.onInitialize(getMod(), "Client", () -> {
			Events.onInitialize(getMod());
			LuminanceConfig.onInitialize(getMod());
			ResourcePacks.onInitialize(getMod());
			Keybindings.onInitialize(getMod());
			CompatHelper.onInitialize(getMod());
			Shaders.onInitialize(getMod());
			ClientTickEvents.END_CLIENT_TICK.register((client) -> {
				if (ClientData.minecraft.isGameLoadFinished()) {
					Tick.onTick();
				}
			});
		}, true);
	}
}