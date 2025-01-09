/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.compat;

import com.mclegoman.luminance.client.screen.config.ConfigScreen;
import com.mclegoman.luminance.common.util.DateHelper;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class CatalogueCompat {
	public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
		return new ConfigScreen(MinecraftClient.getInstance().currentScreen, false, DateHelper.isPride());
	}
}