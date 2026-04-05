/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.Uniforms;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.ScrollWheelHandler;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow @Final private ScrollWheelHandler scrollWheelHandler;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), method = "onScroll", cancellable = true)
	private void luminance$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (Uniforms.updatingAlpha()) {
			boolean discreteMouseScroll = ClientData.minecraft.options.discreteMouseScroll().get();
			double mouseWheelSensitivity = ClientData.minecraft.options.mouseWheelSensitivity().get();
			double h = (discreteMouseScroll ? Math.signum(horizontal) : horizontal) * mouseWheelSensitivity;
			double v = (discreteMouseScroll ? Math.signum(vertical) : vertical) * mouseWheelSensitivity;
			if (ClientData.minecraft.player != null) {
				Vector2i scroll = this.scrollWheelHandler.onMouseScroll(h, v);
				if (scroll.x == 0 && scroll.y == 0) return;
				int scrollAmount = scroll.y == 0 ? -scroll.x : scroll.y;
				Uniforms.adjustAlpha(scrollAmount);
				ci.cancel();
			}
		}
	}
	@Inject(at = @At("HEAD"), method = "onButton", cancellable = true)
	private void luminance$onMouseButton(long window, MouseButtonInfo input, int action, CallbackInfo ci) {
		if (Uniforms.updatingAlpha()) {
			if (input.button() == 2) {
				Uniforms.resetAlpha();
				ci.cancel();
			}
		}
	}
}