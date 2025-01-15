/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.entrypoint;

import com.mclegoman.luminance.entrypoint.LuminanceEntrypoint;
import com.mclegoman.luminance.entrypoint.LuminanceInit;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registries.class)
public abstract class RegistriesMixin {
	@Inject(method = "bootstrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registries;freezeRegistries()V"))
	private static void luminance$onInitialize(CallbackInfo ci) {
		BootstrapAccessor.luminance$invokeSetOutputStreams();
		LuminanceEntrypoint.init(LuminanceEntrypoint.commonInitKey, LuminanceInit.class, LuminanceInit::init);
	}
}
