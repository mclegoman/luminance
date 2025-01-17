/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.entrypoint.client;

import com.mclegoman.luminance.api.entrypoint.LuminanceEntrypoint;
import com.mclegoman.luminance.api.entrypoint.LuminanceEntrypointKeys;
import com.mclegoman.luminance.api.entrypoint.LuminanceInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;<init>(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;)V"))
	private void luminance$clientInit(RunArgs runArgs, CallbackInfo ci) {
		LuminanceEntrypoint.init(LuminanceEntrypointKeys.clientInitKey, LuminanceInit.class, LuminanceInit::init);
	}
}