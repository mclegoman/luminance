/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.hud;

import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(priority = 100, value = DebugHud.class)
public abstract class DebugHudMixin {
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