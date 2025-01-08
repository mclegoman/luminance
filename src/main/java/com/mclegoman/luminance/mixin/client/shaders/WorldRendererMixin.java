/*
    Luminance
    Contributor(s): MCLegoMan, Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.Pool;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", ordinal = 1))
	private void luminance$copyDepth(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Local SimpleFramebufferFactory simpleFramebufferFactory, @Share("depthBackup") LocalRef<Framebuffer> depthBackup, @Share("factory") LocalRef<SimpleFramebufferFactory> factory) {
		RenderPass renderPass = frameGraphBuilder.createPass("copyDepth");
		((FramePassInterface)renderPass).luminance$setForceVisit(true);

		renderPass.setRenderer(() -> {
			Pool pool = ((GameRendererAccessor)gameRenderer).getPool();
			depthBackup.set(pool.acquire(simpleFramebufferFactory));
			depthBackup.get().copyDepthFrom(framebufferSet.mainFramebuffer.get());
		});
		factory.set(simpleFramebufferFactory);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void luminance$restoreDepth(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("depthBackup") LocalRef<Framebuffer> depthBackup, @Share("factory") LocalRef<SimpleFramebufferFactory> factory) {
		RenderPass renderPass = frameGraphBuilder.createPass("restoreDepth");
		((FramePassInterface)renderPass).luminance$setForceVisit(true);

		renderPass.setRenderer(() -> {
			Pool pool = ((GameRendererAccessor)gameRenderer).getPool();
			framebufferSet.mainFramebuffer.get().copyDepthFrom(depthBackup.get());
			pool.release(factory.get(), depthBackup.get());
		});
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/Fog;)V", shift = At.Shift.AFTER))
	private void luminance$afterRenderWeather(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("depthBackup") LocalRef<Framebuffer> depthBackup) {
		//if (postEffectProcessor == null) {
		//	this.framebufferSet.translucentFramebuffer = frameGraphBuilder.createResourceHandle("translucent", simpleFramebufferFactory);
		//	this.framebufferSet.itemEntityFramebuffer = frameGraphBuilder.createResourceHandle("item_entity", simpleFramebufferFactory);
		//	this.framebufferSet.particlesFramebuffer = frameGraphBuilder.createResourceHandle("particles", simpleFramebufferFactory);
		//	this.framebufferSet.weatherFramebuffer = frameGraphBuilder.createResourceHandle("weather", simpleFramebufferFactory);
		//	this.framebufferSet.cloudsFramebuffer = frameGraphBuilder.createResourceHandle("clouds", simpleFramebufferFactory);
		//}
		Execute.afterWeatherRender(frameGraphBuilder, this.framebufferSet);
		//if (postEffectProcessor == null) {
		//	this.framebufferSet.translucentFramebuffer = null;
		//	this.framebufferSet.itemEntityFramebuffer = null;
		//	this.framebufferSet.particlesFramebuffer = null;
		//	this.framebufferSet.weatherFramebuffer = null;
		//	this.framebufferSet.cloudsFramebuffer = null;
		//}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void luminance$afterRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean bl, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		Execute.afterWorldRender(allocator);
	}
}