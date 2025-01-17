/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.entrypoint.server;

import com.mclegoman.luminance.api.entrypoint.LuminanceEntrypoint;
import com.mclegoman.luminance.api.entrypoint.LuminanceEntrypointKeys;
import com.mclegoman.luminance.api.entrypoint.LuminanceInit;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin {
	@Inject(method = "main", at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/lang/String;)V", ordinal = 0), remap = false)
	private static void onInit(String[] strings, CallbackInfo ci) {
		LuminanceEntrypoint.init(LuminanceEntrypointKeys.serverInitKey, LuminanceInit.class, LuminanceInit::init);
	}
}