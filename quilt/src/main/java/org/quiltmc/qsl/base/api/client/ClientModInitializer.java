/*
    quilt_base/entrypoint
    Github: https://github.com/QuiltMC/quilt-standard-libraries
    Licence: Apache License 2.0
*/

package org.quiltmc.qsl.base.api.client;

import org.quiltmc.loader.api.ModContainer;

public interface ClientModInitializer {
	String ENTRYPOINT_KEY = "client_init";
	void onInitializeClient(ModContainer modContainer);
}