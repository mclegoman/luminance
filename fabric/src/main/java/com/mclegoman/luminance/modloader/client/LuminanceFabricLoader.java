/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.modloader.client;

import com.mclegoman.luminance.api.entrypoint.LuminanceInit;
import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.util.Tick;

public class LuminanceFabricLoader implements LuminanceInit {
    public void init(String modId) {
        Tick.init();
        LuminanceClient.init(modId);
    }
}