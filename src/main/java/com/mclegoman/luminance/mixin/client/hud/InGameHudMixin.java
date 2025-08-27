/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.hud;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = InGameHud.class)
public abstract class InGameHudMixin {
	@Inject(at = @At(value = "HEAD"), method = "render")
	private void luminance$renderBefore(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (!ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
			Execute.beforeInGameHudRender(context, tickCounter);
		}
	}
	@Inject(at = @At(value = "TAIL"), method = "render")
	private void luminance$renderAfter(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (!ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
			Execute.afterInGameHudRender(context, tickCounter);
		}
	}
}