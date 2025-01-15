/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.Runnables;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Shaders {
	public static final List<ShaderRegistry> registry = new ArrayList<>();
	public static void init() {
		Uniforms.init();
		Events.BeforeGameRender.register(Identifier.of(Data.version.getID(), "update"), Uniforms::update);

//		Events.AfterHandRender.register(Identifier.of(Data.version.getID(), "main"), new Runnables.GameRender() {
//			@Override
//			public void run(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
//				Events.ShaderRender.registry.forEach((id, shaders) -> {
//					try {
//						if (shaders != null) shaders.forEach(shader -> {
//							try {
//								if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
//									if ((shader.shader().getRenderType().call().equals(Shader.RenderType.WORLD) || (shader.shader().getShaderData().getDisableGameRendertype() || shader.shader().getUseDepth())) && (!shader.shader().getUseDepth() || CompatHelper.isIrisShadersEnabled()))
//										renderUsingAllocator(id, shader, framebuffer, objectAllocator);
//								}
//							} catch (Exception error) {
//								Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterHandRender shader with id: {}:{}", id, error));
//							}
//						});
//					} catch (Exception error) {
//						Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterHandRender shader with id: {}:{}", id, error));
//					}
//				});
//			}
//		});
		// This renders the shader in the world if it has depth. We really should try to render the hand in-depth, but this works for now.
		Events.AfterWeatherRender.register(Identifier.of(Data.version.getID(), "main"), new Runnables.WorldRender() {
			@Override
			public void run(FrameGraphBuilder builder, int textureWidth, int textureHeight, DefaultFramebufferSet framebufferSet) {

				Events.ShaderRender.registry.forEach((id, shaders) -> {
					try {
						if (shaders != null) shaders.forEach(shader -> {
							try {
//								if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
//									if (shader.shader().getUseDepth() && !CompatHelper.isIrisShadersEnabled())
//										renderUsingFramebufferSet(id, shader, builder, textureWidth, textureHeight, framebufferSet);
//								}
								if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
									if (shader.shader().getRenderType().call().equals(Shader.RenderType.WORLD) || shader.shader().getShaderData().getDisableGameRendertype() || shader.shader().getUseDepth())
										renderUsingFramebufferSet(id, shader, builder, textureWidth, textureHeight, framebufferSet);
								}
							} catch (Exception error) {
								Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterWeatherRender shader with id: {}:{}", id, error));
							}
						});
					} catch (Exception error) {
						Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterWeatherRender shader with id: {}:{}", id, error));
					}
				});
			}
		});
		Events.AfterGameRender.register(Identifier.of(Data.version.getID(), "main"), new Runnables.GameRender() {
			@Override
			public void run(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
				Events.ShaderRender.registry.forEach((id, shaders) -> {
					try {
						if (shaders != null) shaders.forEach(shader -> {
							try {
								if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
									if (shader.shader().getRenderType().call().equals(Shader.RenderType.GAME) && !shader.shader().getShaderData().getDisableGameRendertype() && !shader.shader().getUseDepth())
										renderUsingAllocator(id, shader, framebuffer, objectAllocator);
								}
							} catch (Exception error) {
								Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterGameRender shader with id: {}:{}", id, error));
							}
						});
					} catch (Exception error) {
						Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterGameRender shader with id: {}:{}", id, error));
					}
				});
			}
		});
	}
	private static void renderUsingFramebufferSet(Identifier id, Shader.Data shader, FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostProcessor() == null) {
						try {
							shader.shader().setPostProcessor();
						} catch (Exception error) {
							Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					if (shader.shader().getPostProcessor() != null) renderUsingFramebufferSet(shader.shader().getPostProcessor(), builder, textureWidth, textureHeight, framebufferSet);
				}
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render \"{}:{}\" shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
		}
	}
	public static void renderUsingFramebufferSet(PostEffectProcessor processor, FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet) {
		try {
			if (processor != null) {
				try {
					// the depth masking done in renderUsingAllocator is instead done for everything already before this method is called
					// this is because FrameGraphBuilder delays calls, so any rendersystem methods wont work with their intended timing
					processor.render(builder, textureWidth, textureHeight, framebufferSet);
				} catch (Exception error) {
					Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render processor: {}", error.getLocalizedMessage()));
				}
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render post effect processor: {}", error.getLocalizedMessage()));
		}
	}
	private static void renderUsingAllocator(Identifier id, Shader.Data shader, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostProcessor() == null) {
						try {
							shader.shader().setPostProcessor();
						} catch (Exception error) {
							Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					if (shader.shader().getPostProcessor() != null) renderUsingAllocator(shader.shader().getPostProcessor(), framebuffer, objectAllocator);
				}
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render \"{}:{}\" shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
		}
	}
	public static void renderUsingAllocator(PostEffectProcessor processor, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
		try {
			if (processor != null) {
				try {
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					RenderSystem.depthMask(false);
					renderShaderUsingAllocator(processor, framebuffer, objectAllocator);
					RenderSystem.depthMask(true);
					RenderSystem.disableBlend();
				} catch (Exception error) {
					Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render processor: {}", error.getLocalizedMessage()));
				}
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to render post effect processor: {}", error.getLocalizedMessage()));
		}
	}
	public static ShaderRegistry get(int shaderIndex) {
		return isValidIndex(shaderIndex) ? registry.get(shaderIndex) : null;
	}
	public static ShaderRegistry get(Identifier id) {
		int index = getShaderIndex(id);
		return isValidIndex(index) ? get(index) : null;
	}
	public static Shader get(ShaderRegistry shaderData, Callable<Shader.RenderType> renderType, Callable<Boolean> shouldRender) {
		return new Shader(shaderData, renderType, shouldRender);
	}
	public static Shader get(ShaderRegistry shaderData, Callable<Shader.RenderType> renderType) {
		return new Shader(shaderData, renderType);
	}
	public static Identifier getPostShader(Identifier post_effect, boolean full) {
		return Identifier.of(post_effect.getNamespace(), ((full ? "post_effect/" : "") + post_effect.getPath() + (full ? ".json" : "")));
	}
	public static int getShaderIndex(Identifier id) {
		if (id != null) {
			for (ShaderRegistry data : registry) {
				if (data.getID().equals(id)) return registry.indexOf(data);
			}
		}
		return -1;
	}
	public static JsonObject getCustom(int shaderIndex, String namespace) {
		ShaderRegistry shader = get(shaderIndex);
		if (shader != null) {
			JsonObject customData = shader.getCustom();
			if (customData != null) {
				if (customData.has(namespace)) {
					return JsonHelper.getObject(customData, namespace);
				}
			}
		}
		return null;
	}
	public static Text getShaderName(int shaderIndex, boolean shouldShowNamespace) {
		ShaderRegistry shader = get(shaderIndex);
		if (shader != null) return Translation.getShaderTranslation(shader.getID(), shader.getTranslatable(), shouldShowNamespace);
		return Translation.getErrorTranslation(Data.version.getID());
	}
	public static Text getShaderName(int shaderIndex) {
		return getShaderName(shaderIndex, true);
	}
	public static Identifier guessPostShader(String id) {
		// If the shader registry contains at least one shader with the name, the first detected instance will be used.
		if (!id.contains(":")) {
			for (ShaderRegistry registry : registry) {
				if (registry.getID().getPath().equalsIgnoreCase(id)) return registry.getID();
			}
		}
		return Identifier.of(id);
	}
	public static Uniform getUniform(ShaderProgram program, Identifier id) {
		return program.getUniform(getUniformName(id));
	}
	public static String getUniformName(Identifier id) {
		return id.toString();
	}
	public static void set(ShaderProgram program, Identifier id, float... values) {
		try {
			if (program != null) {
				Uniform uniform = getUniform(program, id);
				if (uniform != null) uniform.set(values);
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to set shader uniform: {}_{}: {}", id, error));
		}
	}
	public static void set(ShaderProgram program, Identifier id, Vector3f values) {
		try {
			if (program != null) {
				Uniform uniform = getUniform(program, id);
				if (uniform != null) uniform.set(values);
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to set shader uniform: {}_{}: {}", id, error));
		}
	}
	public static void set(GlUniform uniform, UniformValue uniformValue) {
		uniform.set(uniformValue.values, uniformValue.values.size());
	}
	// This is identical to the deprecated `PostEffectProcessor.render(framebuffer, objectAllocator);` function.
	public static void renderShaderUsingAllocator(PostEffectProcessor processor, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
		FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
		PostEffectProcessor.FramebufferSet framebufferSet = PostEffectProcessor.FramebufferSet.singleton(PostEffectProcessor.MAIN, frameGraphBuilder.createObjectNode("main", framebuffer));
		processor.render(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
		frameGraphBuilder.run(objectAllocator);
	}
	public static int getShaderAmount() {
		return registry.size();
	}
	public static boolean isValidIndex(int index) {
		return index <= getShaderAmount() && index >= 0;
	}
}
