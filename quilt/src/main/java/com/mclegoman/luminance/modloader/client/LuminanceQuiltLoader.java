/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.modloader.client;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.entrypoint.LuminanceInit;

public class LuminanceQuiltLoader implements LuminanceInit {
    public void init(String modId) {
        LuminanceClient.init(modId);
    }
}