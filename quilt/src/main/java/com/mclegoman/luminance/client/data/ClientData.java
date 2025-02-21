/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.data;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import net.minecraft.client.MinecraftClient;
import org.quiltmc.loader.api.QuiltLoader;

public class ClientData {
	public static final MinecraftClient minecraft = MinecraftClient.getInstance();
	public static boolean isDevelopment() {
		return QuiltLoader.isDevelopmentEnvironment() || LuminanceConfig.config.debug.value();
	}
}