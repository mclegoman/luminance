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
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.LuminanceFramebufferSet;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mclegoman.luminance.common.data.Data;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Shadow @Final private LevelTargetBundle targets;

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void luminance$beforeRender(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
		Execute.beforeWorldRender();
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;addPass(Ljava/lang/String;)Lcom/mojang/blaze3d/framegraph/FramePass;"))
	private void luminance$copyFramebuffer(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Local RenderTargetDescriptor simpleFramebufferFactory, @Local PostChain postEffectProcessor, @Share("factory") LocalRef<RenderTargetDescriptor> factory) {
		factory.set(simpleFramebufferFactory);
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain;addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V", ordinal = 1))
	private void luminance$copyDepth(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("depthBackup") LocalRef<RenderTarget> depthBackup, @Share("factory") LocalRef<RenderTargetDescriptor> factory) {
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "copy_depth"), () -> {
			CrossFrameResourcePool pool = ((GameRendererAccessor)ClientData.minecraft.gameRenderer).getResourcePool();
			depthBackup.set(pool.acquire(factory.get()));
			depthBackup.get().copyDepthFrom(targets.main.get());
		});
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain;addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void luminance$restoreDepth(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("depthBackup") LocalRef<RenderTarget> depthBackup, @Share("factory") LocalRef<RenderTargetDescriptor> factory) {
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "restore_depth"), () -> {
			CrossFrameResourcePool pool = ((GameRendererAccessor)ClientData.minecraft.gameRenderer).getResourcePool();
			targets.main.get().copyDepthFrom(depthBackup.get());
			pool.release(factory.get(), depthBackup.get());
		});
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addLateDebugPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/state/CameraRenderState;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
	private void luminance$afterRenderFabulous(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder, @Share("factory") LocalRef<RenderTargetDescriptor> factory) {
		Execute.afterFabulousRender(frameGraphBuilder, LuminanceFramebufferSet.addFabulousIfAbsent(targets, frameGraphBuilder, factory.get()));
	}

	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void luminance$afterRender(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
		Execute.afterWorldRender(allocator);
	}
}