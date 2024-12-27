/*
    Luminance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.events.Execute;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = WorldRenderer.class)
public abstract class WorldRendererMixin {
	@Shadow @Final private DefaultFramebufferSet framebufferSet;

	@Inject(method = "render", at = @At("HEAD"))
	private void luminance$beforeRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean bl, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		Execute.beforeWorldRender();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FrameGraphBuilder;createPass(Ljava/lang/String;)Lnet/minecraft/client/render/RenderPass;"))
	private void luminance$enableFramebuffers(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Local SimpleFramebufferFactory simpleFramebufferFactory, @Local PostEffectProcessor postEffectProcessor) {
		if (postEffectProcessor == null) {
			this.framebufferSet.translucentFramebuffer = frameGraphBuilder.createResourceHandle("translucent", simpleFramebufferFactory);
			this.framebufferSet.itemEntityFramebuffer = frameGraphBuilder.createResourceHandle("item_entity", simpleFramebufferFactory);
			this.framebufferSet.particlesFramebuffer = frameGraphBuilder.createResourceHandle("particles", simpleFramebufferFactory);
			this.framebufferSet.weatherFramebuffer = frameGraphBuilder.createResourceHandle("weather", simpleFramebufferFactory);
			this.framebufferSet.cloudsFramebuffer = frameGraphBuilder.createResourceHandle("clouds", simpleFramebufferFactory);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Vec3d;FLnet/minecraft/client/render/Fog;)V", shift = At.Shift.AFTER))
	private void luminance$afterRenderWeather(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder) {
		Execute.afterWeatherRender(frameGraphBuilder, this.framebufferSet);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void luminance$afterRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean bl, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		Execute.afterWorldRender(allocator);
	}
}