/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package org.quiltmc.qsl.base.api.client;

import org.quiltmc.loader.api.ModContainer;

public interface ClientModInitializer {
	String key = "client_init";
	void onInitializeClient(ModContainer modContainer);
}