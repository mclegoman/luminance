/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.RenderTypes;
import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.resources.Identifier;

import java.util.*;

public class Events {
	public static class GenericRegistry<K, V> {
		public final Map<K, V> registry = new HashMap<>();
		public void register(K key, V value) {
			if (!registry.containsKey(key)) registry.put(key, value);
		}
		public V get(K key) {
			return registry.get(key);
		}
		public void modify(K key, V value) {
			registry.replace(key, value);
		}
		public void remove(K key) {
			registry.remove(key);
		}
	}

	public static class Registry<T> extends GenericRegistry<Identifier, T> {}

	public static final Registry<PreparableReloadListener> ClientResourceReloaders = new Registry<>();
	public static final Registry<Runnable> AfterClientResourceReload = new Registry<>();

	public static final Registry<Runnable> OnShaderDataReset = new Registry<>();
	public static final Registry<Runnables.ShaderData> OnShaderDataRegistered = new Registry<>();
	public static final Registry<Runnables.ShaderData> OnShaderDataRemoved = new Registry<>();
	public static final Registry<Runnable> AfterShaderDataRegistered = new Registry<>();
	public static final Registry<Runnable> AfterShaderStacksRegistered = new Registry<>();

	public static final Registry<Runnables.InGameHudRender> BeforeInGameHudRender = new Registry<>();
	public static final Registry<Runnables.InGameHudRender> AfterInGameHudRender = new Registry<>();
	public static final Registry<Runnable> BeforeWorldRender = new Registry<>();
	public static final Registry<Runnables.WorldRender> AfterFabulousRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterWorldRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterVanillaPostEffectRender = new Registry<>();
	public static final Registry<Runnable> BeforeGameRender = new Registry<>();
	public static final Registry<Runnables.GameRender> BeforeUiRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterUiRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterUiBackgroundRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterPanoramaRender = new Registry<>();

	public static final Registry<Runnables.OnResized> OnResized = new Registry<>();

	public static final Registry<SpectatorHandler> SpectatorHandlers = new Registry<>();

	public static final Registry<Runnables.Shader> BeforeShaderRender = new Registry<>();
	public static final Registry<Runnables.Shader> AfterShaderRender = new Registry<>();

	public static final Registry<Uniform> ShaderUniform = new Registry<>();
	public static final Registry<RenderTypes.RenderType> RenderType = new Registry<>();

	public static class ShaderRender {
		public static final Map<Identifier, ShaderRenderData> registry = new HashMap<>();

		public static void register(Identifier id, ShaderRenderData shaders) {
			if (!registry.containsKey(id)) registry.put(id, shaders);
		}

		public static void register(Identifier id) {
			if (!registry.containsKey(id)) registry.put(id, null);
		}

		public static ShaderRenderData get(Identifier id) {
			return exists(id) ? registry.get(id) : null;
		}

		public static boolean exists(Identifier id) {
			return registry.containsKey(id);
		}

		public static void modify(Identifier id, ShaderRenderData shaders) {
			registry.replace(id, shaders);
		}

		public static void remove(Identifier id) {
			registry.remove(id);
		}

		public static class Shaders {
			// Using these functions is optional, but makes it easier for mod developers to add shaders to their shader list.
			public static boolean register(Identifier registryId, Identifier shaderId, Shader shader) {
				if (ShaderRender.exists(registryId)) {
					ShaderRenderData renderData = ShaderRender.get(registryId);
					if (renderData != null) {
						List<Shader.Data> shaders = renderData.shaders;
						if (shaders == null) shaders = new ArrayList<>();
						for (Shader.Data data : shaders) {
							if (data.id().equals(shaderId)) {
								return false;
							}
						}
						shaders.add(new Shader.Data(shaderId, shader));
						ShaderRender.modify(registryId, new ShaderRenderData(shaders, renderData.disablePhotosensitive()));
						return true;
					}
				}
				return false;
			}

			public static Shader.Data get(Identifier registryId, Identifier shaderId) {
				if (ShaderRender.exists(registryId)) {
					ShaderRenderData renderData = ShaderRender.get(registryId);
					if (renderData != null) {
						List<Shader.Data> shaders = renderData.shaders;
						if (shaders == null) shaders = new ArrayList<>();
						for (Shader.Data data : shaders) {
							if (data.id().equals(shaderId)) {
								return data;
							}
						}
					}
				}
				return null;
			}

			public static boolean modify(Identifier registryId, Identifier shaderId, Shader shader) {
				try {
					ShaderRenderData renderData = ShaderRender.get(registryId);
					if (renderData != null) {
						List<Shader.Data> shaders = renderData.shaders;
						if (shaders != null) {
							for (Shader.Data data : shaders) {
								if (data.id().equals(shaderId)) {
									shaders.set(shaders.indexOf(data), new Shader.Data(shaderId, shader));
									break;
								}
							}
						}
						ShaderRender.modify(registryId, new ShaderRenderData(shaders, renderData.disablePhotosensitive()));
						return true;
					}
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, "Failed to set shader: {}:{}: {}", registryId, shaderId, error);
				}
				return false;
			}

			public static boolean set(Identifier registryId, Identifier shaderId, Shader shader) {
				Callables.ShaderRegistryCaller disablePhotosensitive = ShaderRender.exists(registryId) ? Objects.requireNonNull(ShaderRender.get(registryId)).disablePhotosensitive() : (shaderRegistryEntry) -> false;
				return set(registryId, shaderId, shader, disablePhotosensitive);
			}

			public static boolean set(Identifier registryId, Identifier shaderId, Shader shader, Callables.ShaderRegistryCaller disablePhotosensitive) {
				try {
					if (!ShaderRender.exists(registryId)) ShaderRender.register(registryId, new ShaderRenderData(new ArrayList<>(), disablePhotosensitive));
					return !exists(registryId, shaderId) ? register(registryId, shaderId, shader) : modify(registryId, shaderId, shader);
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, "Failed to set shader: {}:{}: {}", registryId, shaderId, error);
				}
				return false;
			}

			public static boolean exists(Identifier registryId, Identifier shaderId) {
				if (ShaderRender.exists(registryId)) {
					ShaderRenderData renderData = ShaderRender.get(registryId);
					if (renderData != null) {
						List<Shader.Data> shaders = renderData.shaders;
						if (shaders != null) {
							for (Shader.Data data : shaders) {
								if (data.id().equals(shaderId)) return true;
							}
						}
					}
				} return false;
			}

			public static boolean remove(Identifier registryId, Identifier shaderId) {
				ShaderRenderData renderData = ShaderRender.get(registryId);
				if (renderData != null) {
					List<Shader.Data> shaders = renderData.shaders;
					if (shaders != null) {
						if (shaders.removeIf(shaderData -> shaderData.id().equals(shaderId))) {
							ShaderRender.modify(registryId, new ShaderRenderData(shaders, renderData.disablePhotosensitive()));
							return true;
						}
					}
				}
				return false;
			}
		}

		public static boolean remove(Identifier registryId, Shader.Data shader) {
			ShaderRenderData renderData = ShaderRender.get(registryId);
			if (renderData != null) {
				List<Shader.Data> shaders = renderData.shaders;
			if (shaders != null) return shaders.remove(shader);
			}
			return false;
		}
	}

	public record ShaderRenderData(List<Shader.Data> shaders, Callables.ShaderRegistryCaller disablePhotosensitive) {
	}
}
