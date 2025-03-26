/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.resources;

import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow @Final private ReloadableResourceManagerImpl resourceManager;
	@Shadow @Final public GameRenderer gameRenderer;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/LanguageManager;<init>(Ljava/lang/String;Ljava/util/function/Consumer;)V"))
	private void luminance$clientInit(RunArgs runArgs, CallbackInfo ci) {
		Execute.registerClientResourceReloaders(resourceManager);
	}

	@Inject(method = "onFinishedLoading", at = @At("HEAD"))
	private void luminance$finishedLoading(CallbackInfo ci) {
		Execute.afterClientResourceReload();
	}

	@Inject(at = @At("TAIL"), method = "setCameraEntity")
	void onCameraEntitySet(Entity entity, CallbackInfo ci) {
		Execute.onCameraEntitySet(entity);
		if (!SpectatorHandler.activeHandlers.isEmpty()) {
			gameRenderer.onCameraEntitySet(null);
		}
	}

	@Inject(at = @At("TAIL"), method = "joinWorld")
	void onJoinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
		Execute.onJoinWorld();
	}

	@Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V")
	void onDisconnect(CallbackInfo ci) {
		Execute.onDisconnect();
	}
}