/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package org.quiltmc.qsl.base.api.server;

import org.quiltmc.loader.api.ModContainer;

public interface DedicatedServerModInitializer {
	String key = "server_init";
	void onInitializeServer(ModContainer modContainer);
}