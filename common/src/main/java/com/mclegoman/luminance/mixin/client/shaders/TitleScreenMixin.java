/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = TitleScreen.class)
public abstract class TitleScreenMixin {
	@Inject(method = "renderBackground", at = @At("RETURN"))
	private void luminance$afterBackgroundRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		Execute.afterScreenBackgroundRender(((GameRendererAccessor) ClientData.minecraft.gameRenderer).getPool());
	}
}