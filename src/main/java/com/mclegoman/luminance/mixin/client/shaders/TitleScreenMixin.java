/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.gui.screen.LuminanceTitleScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = TitleScreen.class)
public abstract class TitleScreenMixin implements LuminanceTitleScreen {
	@Inject(method = "renderBackground", at = @At("RETURN"))
	private void luminance$afterBackgroundRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		Execute.afterUiBackgroundRender(((GameRendererAccessor) ClientData.minecraft.gameRenderer).getPool());
	}

	@Unique
	private float luminance$backgroundAlpha;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V"))
	private void luminance$setBackgroundAlpha(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci, @Local(name = "f") float f) {
		this.luminance$backgroundAlpha = f;
	}

	public float luminance$getBackgroundAlpha() {
		return this.luminance$backgroundAlpha;
	}
}