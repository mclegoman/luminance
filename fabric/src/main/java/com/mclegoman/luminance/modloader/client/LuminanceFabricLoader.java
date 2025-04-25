/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.modloader.client;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.util.Tick;
import net.fabricmc.api.ClientModInitializer;

public class LuminanceFabricLoader implements ClientModInitializer {
    public void onInitializeClient() {
        Tick.init();
        LuminanceClient.init();
    }
}