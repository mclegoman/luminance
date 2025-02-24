/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.Runnables;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectProcessorInterface;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Shaders {
	protected static final Map<Identifier, List<ShaderRegistryEntry>> registries = new HashMap<>();
	public static void init() {
		Events.ClientResourceReload.register(Identifier.of(Data.getVersion().getID(), "shaders"), new ShaderDataloader());
		Uniforms.init();
		Events.BeforeGameRender.register(Identifier.of(Data.getVersion().getID(), "update"), Uniforms::update);
		Events.AfterHandRender.register(Identifier.of(Data.getVersion().getID(), "main"), (framebuffer, objectAllocator) -> Events.ShaderRender.registry.forEach((id, shaders) -> {
            try {
                if (shaders != null) shaders.forEach(shader -> {
                    try {
                        if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
                            if (((shader.shader().getRenderType().call().equals(Shader.RenderType.WORLD) || (shader.shader().getRenderType().call().equals(Shader.RenderType.GAME) && (shader.shader().getShaderData().getDisableGameRendertype() || shader.shader().getUseDepth()))) && (!shader.shader().getUseDepth() || CompatHelper.isIrisShadersEnabled())) && !ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
                                renderUsingAllocator(id, shader, framebuffer, objectAllocator);
                            }
                        }
                    } catch (Exception error) {
                        Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterHandRender shader with id: {}:{}", id, error));
                    }
                });
            } catch (Exception error) {
                Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterHandRender shader with id: {}:{}", id, error));
            }
        }));
		// This renders the shader in the world if it has depth. We really should try to render the hand in-depth, but this works for now.
		Events.AfterWeatherRender.register(Identifier.of(Data.getVersion().getID(), "main"), (builder, textureWidth, textureHeight, framebufferSet) -> Events.ShaderRender.registry.forEach((id, shaders) -> {
            try {
                if (shaders != null) shaders.forEach(shader -> {
                    try {
                        if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
                            if (((shader.shader().getRenderType().call().equals(Shader.RenderType.WORLD) || (shader.shader().getRenderType().call().equals(Shader.RenderType.GAME) && (shader.shader().getShaderData().getDisableGameRendertype() || shader.shader().getUseDepth()))) && (shader.shader().getUseDepth() && !CompatHelper.isIrisShadersEnabled())) || ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
                                renderUsingFramebufferSet(id, shader, builder, textureWidth, textureHeight, framebufferSet);
                            }
                        }
                    } catch (Exception error) {
                        Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterWeatherRender shader with id: {}:{}", id, error));
                    }
                });
            } catch (Exception error) {
                Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterWeatherRender shader with id: {}:{}", id, error));
            }
        }));
		Events.AfterGameRender.register(Identifier.of(Data.getVersion().getID(), "main"), (framebuffer, objectAllocator) -> Events.ShaderRender.registry.forEach((id, shaders) -> {
			try {
				if (shaders != null) shaders.forEach(shader -> {
					try {
						if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
							if ((shader.shader().getRenderType().call().equals(Shader.RenderType.GAME) && !shader.shader().getShaderData().getDisableGameRendertype() && !shader.shader().getUseDepth()) && !ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
								renderUsingAllocator(id, shader, framebuffer, objectAllocator);
							}
						}
					} catch (Exception error) {
						Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterGameRender shader with id: {}:{}", id, error));
					}
				});
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterGameRender shader with id: {}:{}", id, error));
			}
		}));
		Events.AfterScreenBackgroundRender.register(Identifier.of(Data.getVersion().getID(), "main"), (framebuffer, objectAllocator) -> Events.ShaderRender.registry.forEach((id, shaders) -> {
			try {
				if (shaders != null) shaders.forEach(shader -> {
					try {
						if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
							if (shader.shader().getRenderType().call().equals(Shader.RenderType.SCREEN_BACKGROUND) && !shader.shader().getUseDepth() && !ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
								renderUsingAllocator(id, shader, framebuffer, objectAllocator);
							}
						}
					} catch (Exception error) {
						Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterBackgroundRender shader with id: {}:{}", id, error));
					}
				});
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterBackgroundRender shader with id: {}:{}", id, error));
			}
		}));
		Events.AfterPanoramaRender.register(Identifier.of(Data.getVersion().getID(), "main"), (framebuffer, objectAllocator) -> Events.ShaderRender.registry.forEach((id, shaders) -> {
			try {
				if (shaders != null) shaders.forEach(shader -> {
					try {
						if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
							if (shader.shader().getRenderType().call().equals(Shader.RenderType.PANORAMA) && !shader.shader().getUseDepth() && !ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
								renderUsingAllocator(id, shader, framebuffer, objectAllocator);
							}
						}
					} catch (Exception error) {
						Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterPanoramaRender shader with id: {}:{}", id, error));
					}
				});
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render AfterPanoramaRender shader with id: {}:{}", id, error));
			}
		}));
	}
	public static Identifier getMainRegistryId() {
		return Identifier.of(Data.getVersion().getID(), "main");
	}
	public static List<ShaderRegistryEntry> getRegistry() {
		return getRegistry(getMainRegistryId());
	}
	public static List<ShaderRegistryEntry> getRegistry(Identifier registry) {
		if (!registries.containsKey(registry)) registries.put(registry, new ArrayList<>());
		return registries.get(registry);
	}
	private static void renderUsingFramebufferSet(Identifier id, Shader.Data shader, FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostProcessor() == null) {
						try {
							shader.shader().setPostProcessor();
						} catch (Exception error) {
							Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					renderProcessorUsingFramebufferSet(shader.shader(), builder, textureWidth, textureHeight, framebufferSet, null);
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render \"{}:{}\" using framebuffer set, shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
		}
	}
	public static void renderProcessorUsingFramebufferSet(Shader shader, FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet, @Nullable Identifier customPasses) {
		try {
			if (shader.getPostProcessor() != null) {
				try {
					// the depth masking done in renderUsingAllocator is instead done for everything already before this method is called
					// this is because FrameGraphBuilder delays calls, so any rendersystem methods wont work with their intended timing
					((PostEffectProcessorInterface)shader.getPostProcessor()).luminance$render(builder, textureWidth, textureHeight, framebufferSet, customPasses);
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render processor: {}", error.getLocalizedMessage()));
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render post effect processor: {}", error.getLocalizedMessage()));
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
							Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					renderShaderUsingAllocator(shader.shader(), framebuffer, objectAllocator, null);
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render \"{}:{}\" using allocator, shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error));
		}
	}
	public static ShaderRegistryEntry get(int shaderIndex) {
		return get(getMainRegistryId(), shaderIndex);
	}
	public static ShaderRegistryEntry get(Identifier registry, int shaderIndex) {
		return isValidIndex(registry, shaderIndex) ? getRegistry(registry).get(shaderIndex) : null;
	}
	public static ShaderRegistryEntry get(Identifier shaderId) {
		return get(getMainRegistryId(), shaderId);
	}
	public static ShaderRegistryEntry get(Identifier registry, Identifier shaderId) {
		int index = getShaderIndex(registry, shaderId);
		return isValidIndex(registry, index) ? get(registry, index) : null;
	}
	public static Shader get(ShaderRegistryEntry shaderData, Callable<Shader.RenderType> renderType, Callable<Boolean> shouldRender) {
		return new Shader(shaderData, renderType, shouldRender);
	}
	public static Shader get(ShaderRegistryEntry shaderData, Callable<Shader.RenderType> renderType) {
		return new Shader(shaderData, renderType);
	}
	public static Identifier getPostShader(Identifier post_effect, boolean full) {
		return Identifier.of(post_effect.getNamespace(), ((full ? "post_effect/" : "") + post_effect.getPath() + (full ? ".json" : "")));
	}
	public static int getShaderIndex(Identifier shaderId) {
		return getShaderIndex(getMainRegistryId(), shaderId);
	}
	public static int getShaderIndex(Identifier registry, Identifier shaderId) {
		if (shaderId != null) {
			for (ShaderRegistryEntry data : getRegistry(registry)) {
				if (data.getID().equals(shaderId)) return getRegistry(registry).indexOf(data);
			}
		}
		return -1;
	}
	public static JsonObject getCustom(int shaderIndex, String namespace) {
		return getCustom(getMainRegistryId(), shaderIndex, namespace);
	}
	public static JsonObject getCustom(Identifier registry, int shaderIndex, String namespace) {
		ShaderRegistryEntry shader = get(registry, shaderIndex);
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
		return getShaderName(getMainRegistryId(), shaderIndex, shouldShowNamespace);
	}
	public static Text getShaderName(Identifier registry, int shaderIndex, boolean shouldShowNamespace) {
		ShaderRegistryEntry shader = get(registry, shaderIndex);
		if (shader != null) return Translation.getShaderText(shader.getID(), false, shader.getTranslatable(), shouldShowNamespace);
		return Translation.getErrorTranslation(Data.getVersion().getID());
	}
	public static Text getShaderName(int shaderIndex) {
		return getShaderName(getMainRegistryId(), shaderIndex);
	}
	public static Text getShaderName(Identifier registry, int shaderIndex) {
		return getShaderName(registry, shaderIndex, true);
	}
	public static Text getShaderDescription(int shaderIndex, boolean shouldShowNamespace) {
		return getShaderDescription(getMainRegistryId(), shaderIndex, shouldShowNamespace);
	}
	public static Text getShaderDescription(Identifier registry, int shaderIndex, boolean shouldShowNamespace) {
		ShaderRegistryEntry shader = get(registry, shaderIndex);
		if (shader != null) return Translation.getShaderText(shader.getID(), true, shader.getTranslatable(), shouldShowNamespace);
		return Translation.getErrorTranslation(Data.getVersion().getID());
	}
	public static Text getShaderDescription(int shaderIndex) {
		return getShaderDescription(getMainRegistryId(), shaderIndex);
	}
	public static Text getShaderDescription(Identifier registry, int shaderIndex) {
		return getShaderDescription(registry, shaderIndex, true);
	}
	@Nullable
	public static Identifier guessPostShader(String id) {
		return guessPostShader(getMainRegistryId(), id);
	}
	@Nullable
	public static Identifier guessPostShader(Identifier registry, String id) {
		// If the shader registry contains at least one shader with the name, the first detected instance will be used.
		if (!id.contains(":")) {
			for (ShaderRegistryEntry entry : getRegistry(registry)) {
				if (entry.getID().getPath().equalsIgnoreCase(id)) return entry.getID();
			}
		}
		return Identifier.tryParse(id);
	}
	@Nullable
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
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set shader uniform: {}_{}: {}", id, error));
		}
	}
	public static void set(ShaderProgram program, Identifier id, Vector3f values) {
		try {
			if (program != null) {
				Uniform uniform = getUniform(program, id);
				if (uniform != null) uniform.set(values);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set shader uniform: {}_{}: {}", id, error));
		}
	}
	public static void set(GlUniform uniform, UniformValue uniformValue) {
		uniform.set(uniformValue.values, uniformValue.values.size());
	}
	// This is identical to the deprecated `PostEffectProcessor.render(framebuffer, objectAllocator);` function.
	public static void renderShaderUsingAllocator(Shader shader, Framebuffer framebuffer, ObjectAllocator objectAllocator, @Nullable Identifier customPasses) {
		try {
			if (shader.getPostProcessor() != null) {
				Runnables.WorldRender.fromGameRender((builder, width, height, set) -> ((PostEffectProcessorInterface)shader.getPostProcessor()).luminance$render(builder, width, height, set, customPasses), framebuffer, objectAllocator);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render processor: {}", error.getLocalizedMessage()));
		}
	}
	public static int getShaderAmount() {
		return getShaderAmount(getMainRegistryId());
	}
	public static int getShaderAmount(Identifier registry) {
		return getRegistry(registry).size();
	}
	public static boolean isValidIndex(int index) {
		return isValidIndex(getMainRegistryId(), index);
	}
	public static boolean isValidIndex(Identifier registry, int index) {
		return index <= getShaderAmount(registry) && index >= 0;
	}
	protected static void applyDebugShader() {
		try {
			if (ClientData.isDevelopment()) {
				Events.ShaderRender.register(Identifier.of(Data.getVersion().getID(), "debug"), new ArrayList<>());
				Events.ShaderRender.modify(Identifier.of(Data.getVersion().getID(), "debug"), List.of(new Shader.Data(Identifier.of(Data.getVersion().getID(), "debug"), new Shader(get(Identifier.of("luminance", "debug"), Identifier.of("luminance", "debug")), () -> Debug.debugRenderType, () -> Debug.debugShader))));
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to apply debug shader: {}", error));
		}
	}
}
