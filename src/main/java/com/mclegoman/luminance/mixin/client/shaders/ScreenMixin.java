/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = Screen.class)
public abstract class ScreenMixin {
	@Inject(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderBlurredBackground(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void luminance$afterBackgroundRender_inWorldBeforeBlur(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		if (ClientData.minecraft.level != null) Execute.afterUiBackgroundRender(((GameRendererAccessor) ClientData.minecraft.gameRenderer).getResourcePool());
	}

	@Inject(method = "renderTransparentBackground", at = @At("RETURN"))
	private void luminance$afterBackgroundRender_afterInGameBackground(GuiGraphics context, CallbackInfo ci) {
		if (ClientData.minecraft.level != null) Execute.afterUiBackgroundRender(((GameRendererAccessor) ClientData.minecraft.gameRenderer).getResourcePool());
	}

	@Inject(method = "renderPanorama", at = @At("RETURN"))
	private void luminance$afterBackgroundRender_notInWorld(GuiGraphics context, float deltaTicks, CallbackInfo ci) {
		if (ClientData.minecraft.level == null) Execute.afterUiBackgroundRender(((GameRendererAccessor) ClientData.minecraft.gameRenderer).getResourcePool());
	}
}