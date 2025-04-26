/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.entrypoint.server;

import net.minecraft.server.Main;
import org.quiltmc.loader.api.entrypoint.EntrypointUtil;
import org.quiltmc.loader.api.minecraft.DedicatedServerOnly;
import org.quiltmc.qsl.base.api.server.DedicatedServerModInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@DedicatedServerOnly
@Mixin(Main.class)
public abstract class MainMixin {
	@Inject(method = "main", at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/lang/String;)V", ordinal = 0), remap = false)
	private static void onInit(String[] strings, CallbackInfo ci) {
		EntrypointUtil.invoke(DedicatedServerModInitializer.ENTRYPOINT_KEY, DedicatedServerModInitializer.class, DedicatedServerModInitializer::onInitializeServer);
	}
}