/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.compat;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.config.ConfigScreen;
import com.mclegoman.luminance.client.translation.Translation;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;

public class SodiumConfigCompat implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions().
                setName(LuminanceClient.getMod().getName())
                .setIcon(ClientData.getOverrideIcon().getIconId())
                .setVersion(LuminanceClient.getMod().getMetadata().getVersion().getFriendlyString())
                .addPage(builder.createExternalPage().setName(Translation.getTranslation(LuminanceClient.getMod().getId(), "config"))
                        .setScreenConsumer((screen) -> Minecraft.getInstance().setScreen(ConfigScreen.open(screen))));
    }
}
