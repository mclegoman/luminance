/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.resources;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow @Final public GameRenderer gameRenderer;

	@Inject(at = @At("TAIL"), method = "setCameraEntity")
	void onCameraEntitySet(Entity entity, CallbackInfo ci) {
		Execute.onCameraEntitySet(entity);
		if (!SpectatorHandler.activeHandlers.isEmpty() && LuminanceConfig.config.spectatorPriorityMode.value().getMode() != SpectatorHandler.Mode.ALL) {
			gameRenderer.checkEntityPostEffect(null);
		}
	}
}