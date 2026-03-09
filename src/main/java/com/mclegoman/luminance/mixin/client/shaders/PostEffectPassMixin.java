/*
    Luminance
    Contributor(s): Nettakrim, dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.UniformData;
import com.mclegoman.luminance.client.shaders.interfaces.CustomPassData;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectPassInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mclegoman.luminance.client.shaders.overrides.LuminanceUniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.MapConfig;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.gl.*;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(priority = 100, value = PostEffectPass.class)
public abstract class PostEffectPassMixin implements PostEffectPassInterface {
	@Shadow @Final private String id;

	@Shadow @Final private Identifier outputTargetId;
	@Shadow @Final private List<PostEffectPass.Sampler> samplers;

	@Unique private final Map<String, ImmutableList<@NotNull UniformData>> luminance$uniformOverrides = new HashMap<>();
	@Unique private final Map<Identifier, CustomPassData> luminance$customData = new HashMap<>();

	@Inject(method = "method_67884", at = @At("HEAD"))
	private void luminance$beforeRender(Handle<Framebuffer> handle, GpuBufferSlice gpuBufferSlice, Map<Identifier, Handle<Framebuffer>> map, CallbackInfo ci) {
		Execute.beforeShaderRender((PostEffectPass)(Object)this);
	}
	@Inject(method = "method_67884", at = @At("TAIL"))
	private void luminance$afterRender(Handle<Framebuffer> handle, GpuBufferSlice gpuBufferSlice,  Map<Identifier, Handle<Framebuffer>> map, CallbackInfo ci) {
		Execute.afterShaderRender((PostEffectPass)(Object)this);
	}

	@WrapOperation(method = "method_67884", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBuffer;)V", ordinal = 0))
	private void luminance$setUniformValues(RenderPass instance, String key, GpuBuffer gpuBuffer, Operation<Void> original) {
		for (UniformData data : luminance$uniformOverrides.get(key)) {
			List<Float> values = data.getValues();
			// if value is null, value need to be default, otherwise, replace it

			// it seems this will require creating a new gpu buffer? which is a little awkward. how does vanilla do its dynamic stuff?
		}

		original.call(instance, key, gpuBuffer);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initialiseUniformData(RenderPipeline pipeline, Identifier outputTargetId, Map<String, List<UniformValue>> uniforms, List<PostEffectPass.Sampler> samplers, CallbackInfo ci) {
		uniforms.forEach((block, list) -> {
			ImmutableList.Builder<UniformData> builder = ImmutableList.builder();

			for (UniformValue uniform : list) {
				UniformValueInterface uniformInterface = (UniformValueInterface)uniform;
				assert uniformInterface != null;

				UniformData data = new UniformData();

				// TODO:
				//  check if override size doesnt match expected from type and add nulls / truncate to match
				//  config is intended to be uneven
				uniformInterface.luminance$getOverride().ifPresent((override) -> data.override = new LuminanceUniformOverride(override));
				uniformInterface.luminance$getConfig().ifPresent((config) -> data.config = new MapConfig(config));

				builder.add(data);
			}

			luminance$uniformOverrides.put(block, builder.build());
		});
	}

	@Override
	public String luminance$getID() {
		return id;
	}

	@Override
	public ImmutableList<@NotNull UniformData> luminance$getUniformData(String block) {
		return luminance$uniformOverrides.get(block);
	}

	@Override
	public Identifier luminance$getOutputTarget() {
		return outputTargetId;
	}

//	@Unique
//	private boolean luminance$forceVisit;
//
//	@Override
//	public void luminance$setForceVisit(boolean to) {
//		luminance$forceVisit = to;
//	}
//
//	@Inject(at = @At(value = "TAIL"), method = "render")
//	private void forceVisit(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, Matrix4f projectionMatrix, CallbackInfo ci, @Local RenderPass renderPass) {
//		if (luminance$forceVisit) {
//			((FramePassInterface)renderPass).luminance$setForceVisit(true);
//		}
//	}

	@Override
	public CustomPassData luminance$putCustomData(Identifier identifier, CustomPassData data) {
		return luminance$customData.put(identifier, data);
	}

	@Override
	public Optional<CustomPassData> luminance$getCustomData(Identifier identifier) {
		return Optional.ofNullable(luminance$customData.get(identifier));
	}

	@Override
	public boolean luminance$usesDepth() {
		for (PostEffectPass.Sampler sampler : samplers) {
			if (sampler instanceof PostEffectPass.TargetSampler targetSampler && targetSampler.depthBuffer()) {
				return true;
			}
		}
		return false;
	}
}