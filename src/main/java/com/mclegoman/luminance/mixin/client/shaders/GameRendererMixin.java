/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Pool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow @Final private Pool pool;
	@Inject(method = "render", at = @At("HEAD"))
	private void luminance$beforeGameRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		Execute.beforeGameRender();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogRenderer;rotate()V")))
	private void luminance$afterPostRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		Execute.afterVanillaPostEffectRender(this.pool);
	}
	@Inject(method = "render", at = @At("TAIL"))
	private void luminance$afterUiRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		Execute.afterUiRender(this.pool);
	}
	@Inject(method = "onResized", at = @At(value = "TAIL"))
	private void luminance$onResized(int width, int height, CallbackInfo ci) {
		Execute.resize(width, height);
	}
}