/*
    Luminance
    Contributor(s): Nettakrim, dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectPassInterface;
import com.mclegoman.luminance.client.shaders.interfaces.ShaderProgramInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineUniformInterface;
import com.mclegoman.luminance.client.shaders.overrides.LuminanceUniformOverride;
import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.MapConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
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

	@Shadow @Final private ShaderProgram program;

	@Shadow @Final private List<PostEffectPipeline.Uniform> uniforms;

	@Shadow @Final private Identifier outputTargetId;
	@Shadow @Final private List<PostEffectPass.Sampler> samplers;
	@Unique private final Map<String, UniformOverride> luminance$uniformOverrides = new HashMap<>();
	@Unique private final Map<String, UniformConfig> luminance$uniformConfigs = new HashMap<>();
	@Unique private final Map<Identifier, Object> luminance$customData = new HashMap<>();

	@Inject(method = "method_62257", at = @At("HEAD"))
	private void luminance$beforeRender(Handle<Framebuffer> handle, Map<Identifier, Handle<Framebuffer>> map, Matrix4f matrix4f, CallbackInfo ci) {
		Execute.beforeShaderRender((PostEffectPass)(Object)this);
	}
	@Inject(method = "method_62257", at = @At("TAIL"))
	private void luminance$afterRender(Handle<Framebuffer> handle, Map<Identifier, Handle<Framebuffer>> map, Matrix4f matrix4f, CallbackInfo ci) {
		Execute.afterShaderRender((PostEffectPass)(Object)this);
	}

	@Inject(method = "method_62257", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;setClearColor(FFFF)V"))
	private void luminance$setUniformValues(Handle<Framebuffer> handle, Map<Identifier, Handle<Framebuffer>> map, Matrix4f matrix4f, CallbackInfo ci) {
		for (String uniformName : ((ShaderProgramInterface)program).luminance$getUniformNames()) {
			com.mclegoman.luminance.client.shaders.uniforms.Uniform uniform = Events.ShaderUniform.registry.get(uniformName);
			if (uniform == null) {
				continue;
			}

			GlUniform glUniform = program.getUniform(uniformName);
			assert glUniform != null;
			Shaders.set(glUniform, uniform.get(luminance$uniformConfigs.getOrDefault(uniformName, EmptyConfig.INSTANCE), Uniforms.shaderTime));
		}

		luminance$uniformOverrides.forEach((name, override) -> {
			GlUniform glUniform = program.getUniform(name);
			if (glUniform == null) {
				return;
			}

			//the double looping of the same array here is to avoid needing to call luminance$getCurrentUniformValues unless its needed
			List<Float> values = override.getOverride(luminance$uniformConfigs.getOrDefault(name, EmptyConfig.INSTANCE), Uniforms.shaderTime);
            for (Float value : values) {
                if (value == null) {
                    List<Float> current = ((ShaderProgramInterface)program).luminance$getCurrentUniformValues(name);
					for (int i = 0; i < values.size(); i++) {
						if (values.get(i) == null) {
							values.set(i, current.get(i));
						}
					}
                    break;
                }
            }

			glUniform.set(values, values.size());
		});
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initialiseUniformData(String id, ShaderProgram program, Identifier outputTargetId, List<PostEffectPipeline.Uniform> uniforms, CallbackInfo ci) {
		for (PostEffectPipeline.Uniform uniform : uniforms) {
			PipelineUniformInterface data = (PipelineUniformInterface)(Object)uniform;
			assert data != null;

			data.luminance$getOverride().ifPresent((override) -> {
				int uniformSize = program.getUniformDefinition(uniform.name()).count();
				int overrideSize = override.size();

				if (uniformSize != overrideSize) {
					override = new ArrayList<>(override);
					if (overrideSize < uniformSize) {
						for (int i = overrideSize; i < uniformSize; i++) {
							override.add(null);
						}
					} else {
						override.subList(uniformSize, overrideSize).clear();
					}
				}

				luminance$uniformOverrides.put(uniform.name(), new LuminanceUniformOverride(override));
			});

			data.luminance$getConfig().ifPresent((list) -> luminance$uniformConfigs.put(uniform.name(), new MapConfig(list)));
		}
	}

	@Override
	public String luminance$getID() {
		return id;
	}

	@Override
	public List<PostEffectPipeline.Uniform> luminance$getUniforms() {
		return uniforms;
	}

	@Override
	public UniformOverride luminance$getUniformOverride(String uniform) {
		return luminance$uniformOverrides.get(uniform);
	}

	@Override
	public UniformOverride luminance$addUniformOverride(String uniform, UniformOverride override) {
		return luminance$uniformOverrides.put(uniform, override);
	}

	@Override
	public Map<String, UniformConfig> luminance$getUniformConfigs() {
		return luminance$uniformConfigs;
	}

	@Override
	public UniformOverride luminance$removeUniformOverride(String uniform) {
		// removing a uniformOverride for a uniform which is only defined in the shader program and not also the pass causes the value to be left as it was
		// to fix this the uniform is just forcefully reset
		luminance$resetUniform(uniform);

		return luminance$uniformOverrides.remove(uniform);
	}

	@Unique
	private void luminance$resetUniform(String uniformName) {
		// NOTE: this sets it to the value in the shaderprogram, not the posteffectpass
		// this should never cause an issue, since the posteffectpass uniforms are set halfway through method_62257, and used moments later, so the window of time where it can cause a desync is like 15 lines of code

		GlUniform glUniform = program.getUniform(uniformName);
		if (glUniform == null) return;

		List<Float> values = Objects.requireNonNull(program.getUniformDefinition(uniformName)).values();
		glUniform.set(values, values.size());
	}

	@Override
	public Identifier luminance$getOutputTarget() {
		return outputTargetId;
	}

	@Unique
	private boolean luminance$forceVisit;

	@Override
	public void luminance$setForceVisit(boolean to) {
		luminance$forceVisit = to;
	}

	@Inject(at = @At(value = "TAIL"), method = "render")
	private void forceVisit(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, Matrix4f projectionMatrix, CallbackInfo ci, @Local RenderPass renderPass) {
		if (luminance$forceVisit) {
			((FramePassInterface)renderPass).luminance$setForceVisit(true);
		}
	}

	@Override
	public Object luminance$putCustomData(Identifier identifier, Object object) {
		return luminance$customData.put(identifier, object);
	}

	@Override
	public Optional<Object> luminance$getCustomData(Identifier identifier) {
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