/*
    quilt_base/entrypoint
    Github: https://github.com/QuiltMC/quilt-standard-libraries
    Licence: Apache License 2.0
*/

package org.quiltmc.qsl.base.api.server;

import org.quiltmc.loader.api.ModContainer;

public interface DedicatedServerModInitializer {
	String ENTRYPOINT_KEY = "server_init";
	void onInitializeServer(ModContainer modContainer);
}