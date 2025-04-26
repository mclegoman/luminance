/*
    quilt_base/entrypoint
    Github: https://github.com/QuiltMC/quilt-standard-libraries
    Licence: Apache License 2.0
*/

package org.quiltmc.qsl.base.api;

import org.quiltmc.loader.api.ModContainer;

public interface ModInitializer {
	String ENTRYPOINT_KEY = "init";
	void onInitialize(ModContainer modContainer);
}