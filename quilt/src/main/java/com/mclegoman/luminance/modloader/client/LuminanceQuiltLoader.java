/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.modloader.client;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.util.Tick;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.client.ClientModInitializer;

public class LuminanceQuiltLoader implements ClientModInitializer {
    public void onInitializeClient(ModContainer modContainer) {
        Tick.init();
        LuminanceClient.init();
    }
}