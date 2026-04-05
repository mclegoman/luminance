/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.gui;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(priority = 100, value = DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
	// TODO: update
//	@Inject(at = @At("RETURN"), method = "getLeftText", cancellable = true)
//	private void luminance$addToDebugRight(CallbackInfoReturnable<List<String>> cir) {
//		List<String> texts = cir.getReturnValue();
//		if (Debug.debugShaderEnabled) {
//			texts.add("[" + Data.getVersion().getName() + "] Debug: Render Type: " + Debug.debugRenderType.toString() + ", Registry: " + Debug.debugShader.getFirst().toString() + ", Shader: " + Debug.debugShader.getSecond().toString());
//		}
//		cir.setReturnValue(texts);
//	}
}