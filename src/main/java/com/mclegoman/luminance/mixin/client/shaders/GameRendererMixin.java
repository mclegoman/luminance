/*
    Luminance
    Contributor(s): MCLegoMan, Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.events.RenderEvents;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "render", at = @At("TAIL"))
	private void luminance$afterGameRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		RenderEvents.AfterGameRender.registry.forEach(((id, runnable) -> runnable.run()));
	}
	@Inject(method = "onResized", at = @At(value = "TAIL"))
	private void luminance$onResized(int width, int height, CallbackInfo ci) {
		RenderEvents.ShaderRender.registry.forEach((id, shaders) -> shaders.forEach(shader -> shader.getSecond().getPostProcessor().setupDimensions(width, height)));
	}
}