/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow @Final private CrossFrameResourcePool resourcePool;
	@Inject(method = "render", at = @At("HEAD"))
	private void luminance$beforeGameRender(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
		Execute.beforeGameRender();
	}
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;endFrame()V"))
	private void luminance$afterPostRender(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
		Execute.afterVanillaPostEffectRender(this.resourcePool);
	}
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
	private void luminance$beforeUiRender(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
		Execute.beforeUiRender(this.resourcePool);
	}
	@Inject(method = "render", at = @At("TAIL"))
	private void luminance$afterUiRender(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
		Execute.afterUiRender(this.resourcePool);
	}
	@Inject(method = "resize", at = @At(value = "TAIL"))
	private void luminance$onResized(int width, int height, CallbackInfo ci) {
		Execute.resize(width, height);
	}
}