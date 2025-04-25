/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package org.quiltmc.qsl.base.api;

import org.quiltmc.loader.api.ModContainer;

public interface ModInitializer {
	String key = "init";
	void onInitialize(ModContainer modContainer);
}