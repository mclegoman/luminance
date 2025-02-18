/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.PersistentFramebufferFactory;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mclegoman.luminance.common.data.Data;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.Pool;
import net.minecraft.util.Identifier;
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
	private void luminance$beforeRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		Execute.beforeWorldRender();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FrameGraphBuilder;createPass(Ljava/lang/String;)Lnet/minecraft/client/render/RenderPass;"))
	private void luminance$copyFramebuffer(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Local SimpleFramebufferFactory simpleFramebufferFactory, @Local PostEffectProcessor postEffectProcessor, @Share("factory") LocalRef<SimpleFramebufferFactory> factory) {
		factory.set(simpleFramebufferFactory);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", ordinal = 1))
	private void luminance$copyDepth(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("depthBackup") LocalRef<Framebuffer> depthBackup, @Share("factory") LocalRef<SimpleFramebufferFactory> factory) {
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "copy_depth"), () -> {
			Pool pool = ((GameRendererAccessor)gameRenderer).getPool();
			depthBackup.set(pool.acquire(factory.get()));
			depthBackup.get().copyDepthFrom(framebufferSet.mainFramebuffer.get());
		});
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void luminance$restoreDepth(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("depthBackup") LocalRef<Framebuffer> depthBackup, @Share("factory") LocalRef<SimpleFramebufferFactory> factory) {
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "restore_depth"), () -> {
			Pool pool = ((GameRendererAccessor)gameRenderer).getPool();
			framebufferSet.mainFramebuffer.get().copyDepthFrom(depthBackup.get());
			pool.release(factory.get(), depthBackup.get());
		});
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/Fog;)V", shift = At.Shift.AFTER))
	private void luminance$afterRenderWeather(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("factory") LocalRef<SimpleFramebufferFactory> factory) {
		boolean replace = framebufferSet.translucentFramebuffer == null;
		if (replace) {
			PersistentFramebufferFactory persistentFramebufferFactory = new PersistentFramebufferFactory(factory.get(), null, Identifier.of(Data.getVersion().getID(), "fabulous"));
			framebufferSet.translucentFramebuffer = frameGraphBuilder.createResourceHandle("translucent", persistentFramebufferFactory);
			framebufferSet.itemEntityFramebuffer = frameGraphBuilder.createResourceHandle("item_entity", persistentFramebufferFactory);
			framebufferSet.particlesFramebuffer = frameGraphBuilder.createResourceHandle("particles", persistentFramebufferFactory);
			framebufferSet.weatherFramebuffer = frameGraphBuilder.createResourceHandle("weather", persistentFramebufferFactory);
			framebufferSet.cloudsFramebuffer = frameGraphBuilder.createResourceHandle("clouds", persistentFramebufferFactory);
		}

		// frameGraphBuilder delays calls, so the depth masking done in Shaders.renderUsingAllocator needs to be done here instead
		// TODO: potentially move this into Execute? - in any case thisll need to change slightly when we get the hand renderer working
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "prepare_shader_render"), () -> {
			//RenderSystem.enableBlend();
			//RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(false);
		});
		Execute.afterWeatherRender(frameGraphBuilder, framebufferSet);
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "cleanup_shader_render"), () -> {
			RenderSystem.depthMask(true);
			//RenderSystem.disableBlend();
		});

		if (replace) {
			framebufferSet.translucentFramebuffer = null;
			framebufferSet.itemEntityFramebuffer = null;
			framebufferSet.particlesFramebuffer = null;
			framebufferSet.weatherFramebuffer = null;
			framebufferSet.cloudsFramebuffer = null;
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void luminance$afterRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		Execute.afterWorldRender(allocator);
	}
}