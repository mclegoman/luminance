/*
    Luminance
    Contributor(s): Nettakrim, dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.UniformBlock;
import com.mclegoman.luminance.client.shaders.interfaces.CustomPassData;
import com.mclegoman.luminance.client.shaders.interfaces.PostPassInterface;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.resource.ResourceHandle;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(priority = 100, value = PostPass.class)
public abstract class PostPassMixin implements PostPassInterface {
	@Shadow @Final private String name;

	@Shadow @Final private Identifier outputTargetId;
	@Shadow @Final private List<PostPass.Input> inputs;

	@Shadow @Final private Map<String, GpuBuffer> customUniforms;
	@Unique private final Map<Identifier, CustomPassData> luminance$customData = new HashMap<>();

	@Unique private final Map<String, UniformBlock> luminance$overrides = new HashMap<>();

	@Inject(method = "method_67884", at = @At("HEAD"))
	private void luminance$beforeRender(ResourceHandle<RenderTarget> handle, GpuBufferSlice gpuBufferSlice, Map<Identifier, ResourceHandle<RenderTarget>> map, CallbackInfo ci) {
		Execute.beforeShaderRender((PostPass)(Object)this);
	}
	@Inject(method = "method_67884", at = @At("TAIL"))
	private void luminance$afterRender(ResourceHandle<RenderTarget> handle, GpuBufferSlice gpuBufferSlice, Map<Identifier, ResourceHandle<RenderTarget>> map, CallbackInfo ci) {
		Execute.afterShaderRender((PostPass)(Object)this);
	}

	@Inject(method = "method_67884", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalInt;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/blaze3d/systems/RenderPass;"))
	private void luminance$updateBuffers(ResourceHandle<RenderTarget> handle, GpuBufferSlice gpuBufferSlice, Map<Identifier, ResourceHandle<RenderTarget>> map, CallbackInfo ci) {
		luminance$overrides.values().forEach(UniformBlock::updateBuffer);
	}

	@WrapOperation(method = "method_67884", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBuffer;)V", ordinal = 1))
	private void luminance$replaceBuffers(RenderPass instance, String block, GpuBuffer gpuBuffer, Operation<Void> original) {
		original.call(instance, block, luminance$overrides.get(block).replaceBuffer(gpuBuffer));
	}

	@Inject(method = "method_67884", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MappableRingBuffer;rotate()V"))
	private void luminance$rotateBuffers(ResourceHandle<RenderTarget> handle, GpuBufferSlice gpuBufferSlice, Map<Identifier, ResourceHandle<RenderTarget>> map, CallbackInfo ci) {
		luminance$overrides.values().forEach(UniformBlock::rotateBuffer);
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void clearData(CallbackInfo ci) {
		for (UniformBlock uniformBlock : luminance$overrides.values()) {
			uniformBlock.close();
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initialiseUniformData(RenderPipeline pipeline, Identifier outputTargetId, Map<String, List<UniformValue>> uniforms, List<PostPass.Input> samplers, CallbackInfo ci) {
		uniforms.forEach((block, list) -> {
			GpuBuffer buffer = customUniforms.get(block);
			if (buffer != null) {
				luminance$overrides.put(block, new UniformBlock(list, (int) buffer.size()));
			}
		});
	}

	@Override
	public String luminance$getID() {
		return name;
	}

	@Override
	public Set<String> luminance$getUniformBlockNames() {
		return luminance$overrides.keySet();
	}

	@Override
	public UniformBlock luminance$getUniformBlock(String block) {
		return luminance$overrides.get(block);
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
		for (PostPass.Input sampler : inputs) {
			if (sampler instanceof PostPass.TargetInput targetSampler && targetSampler.depthBuffer()) {
				return true;
			}
		}
		return false;
	}
}