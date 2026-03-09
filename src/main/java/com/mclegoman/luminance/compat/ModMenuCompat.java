/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.compat;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.config.ConfigScreen;
import com.mclegoman.luminance.common.util.DateHelper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ConfigScreen(ClientData.minecraft.currentScreen, 0, null, DateHelper.isPride());
	}
}