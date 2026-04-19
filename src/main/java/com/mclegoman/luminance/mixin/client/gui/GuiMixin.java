/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.gui;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = Gui.class)
public abstract class GuiMixin {
	@Inject(at = @At(value = "HEAD"), method = "render")
	private void luminance$renderBefore(GuiGraphics guiGraphics, DeltaTracker tickCounter, CallbackInfo ci) {
		if (!ClientData.minecraft.gameRenderer.isPanoramicMode()) {
			Execute.beforeInGameHudRender(guiGraphics, tickCounter);
		}
	}
	@Inject(at = @At(value = "TAIL"), method = "render")
	private void luminance$renderAfter(GuiGraphics guiGraphics, DeltaTracker tickCounter, CallbackInfo ci) {
		if (!ClientData.minecraft.gameRenderer.isPanoramicMode()) {
			Execute.afterInGameHudRender(guiGraphics, tickCounter);
		}
	}
}