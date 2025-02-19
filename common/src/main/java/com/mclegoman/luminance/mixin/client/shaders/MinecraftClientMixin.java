/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.ShaderDataloader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow @Final private ReloadableResourceManagerImpl resourceManager;
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/LanguageManager;<init>(Ljava/lang/String;Ljava/util/function/Consumer;)V"))
	private void luminance$clientInit(RunArgs runArgs, CallbackInfo ci) {
		this.resourceManager.registerReloader(new ShaderDataloader());
	}
}