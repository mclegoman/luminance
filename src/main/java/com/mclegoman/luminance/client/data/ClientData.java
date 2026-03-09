/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.data;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.MinecraftClient;

public class ClientData {
	public static final MinecraftClient minecraft = MinecraftClient.getInstance();
	public static boolean isDevelopment() {
		return FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment() || LuminanceConfig.config.debug.value();
	}
}