/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.compat;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.config.ConfigScreen;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.MinecraftClient;

public class SodiumConfigCompat implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions().
                setName(Data.getVersion().getName())
                .setIcon(ClientData.getOverrideIcon().getIconId())
                .setVersion(Data.getVersion().getFriendlyString())
                .addPage(builder.createExternalPage().setName(Translation.getTranslation(Data.getVersion().getID(), "config"))
                        .setScreenConsumer((screen) -> MinecraftClient.getInstance().setScreen(ConfigScreen.open(screen))));
    }
}
