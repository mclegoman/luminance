/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.shaders.RenderLocations;
import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.shaders.interfaces.PostChainInterface;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import dev.dannytaylor.perspective.seam.client.events.SeamClientEvents;
import dev.dannytaylor.perspective.seam.client.events.SeamClientRunnables;
import dev.dannytaylor.perspective.seam.common.data.AbstractMod;
import dev.dannytaylor.perspective.seam.common.data.log.SeamLog;
import dev.dannytaylor.perspective.seam.common.events.SeamEvents;
import dev.dannytaylor.perspective.seam.common.events.registries.Registry;
import net.minecraft.resources.Identifier;

import java.util.*;

public class Events extends SeamClientEvents {
	// Registry and Generic Registry have moved to Seam.
	// dev.dannytaylor.perspective.seam.common.events.registries.GenericRegistry<K, V>
	// dev.dannytaylor.perspective.seam.common.events.registries.Registry<Identifier, V>

	public static final Registry<Runnable> OnShaderDataReset = new Registry<>();
	public static final Registry<Runnables.ShaderData> OnShaderDataRegistered = new Registry<>();
	public static final Registry<Runnables.ShaderData> OnShaderDataRemoved = new Registry<>();
	public static final Registry<Runnable> AfterShaderDataRegistered = new Registry<>();
	public static final Registry<Runnable> AfterShaderStacksRegistered = new Registry<>();

	public static final Registry<Runnable> BeforeLevelRender = new Registry<>();
	public static final Registry<Runnables.LevelRender> AfterFabulousRender = new Registry<>();
	public static final Registry<SeamClientRunnables.GameRender> AfterLevelRender = new Registry<>();

	public static final Registry<SpectatorHandler> SpectatorHandlers = new Registry<>();

	public static final Registry<Runnables.Shader> BeforeShaderRender = new Registry<>();
	public static final Registry<Runnables.Shader> AfterShaderRender = new Registry<>();

	public static final Registry<Uniform> ShaderUniform = new Registry<>();
	public static final Registry<RenderLocations.RenderLocation<?>> RenderLocation = new Registry<>();
	public static final Registry<PostChainInterface> CustomPostChains = new Registry<>();

	public static void onInitialize(AbstractMod mod) {
		SeamEvents.onInitialize(mod, "Events", () -> {
			SeamClientEvents.OnJoinWorld.register(mod.idOf("spectator_handler"), Execute::onJoinWorld);
			SeamClientEvents.OnLeaveWorld.register(mod.idOf("spectator_handler"), Execute::onDisconnect);
		});
	}

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
					SeamLog.error(LuminanceClient.getMod(), "Failed to set shader: {}:{}", registryId, shaderId, error);
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
					SeamLog.error(LuminanceClient.getMod(), "Failed to set shader: {}:{}", registryId, shaderId, error);
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
