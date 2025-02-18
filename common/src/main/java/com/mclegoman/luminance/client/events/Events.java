/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static final Registry<Runnable> OnShaderDataReset = new Registry<>();
	public static final Registry<Runnables.ShaderData> OnShaderDataRegistered = new Registry<>();
	public static final Registry<Runnables.ShaderData> OnShaderDataRemoved= new Registry<>();
	public static final Registry<Runnable> AfterShaderDataRegistered = new Registry<>();

	public static final Registry<Runnables.InGameHudRender> BeforeInGameHudRender = new Registry<>();
	public static final Registry<Runnables.InGameHudRender> AfterInGameHudRender = new Registry<>();
	public static final Registry<Runnable> BeforeWorldRender = new Registry<>();
	public static final Registry<Runnables.WorldRender> AfterWeatherRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterWorldRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterHandRender = new Registry<>();
	public static final Registry<Runnable> BeforeGameRender = new Registry<>();
	public static final Registry<Runnables.GameRender> AfterGameRender = new Registry<>();

	public static final Registry<Runnables.OnResized> OnResized = new Registry<>();

	public static final Registry<Runnables.Shader> BeforeShaderRender = new Registry<>();
	public static final Registry<Runnables.Shader> AfterShaderRender = new Registry<>();

	public static final GenericRegistry<String, Uniform> ShaderUniform = new GenericRegistry<>();


	public static class ShaderRender {
		public static final Map<Identifier, List<Shader.Data>> registry = new HashMap<>();
		public static void register(Identifier id, List<Shader.Data> shaders) {
			if (!registry.containsKey(id)) registry.put(id, shaders);
		}
		public static void register(Identifier id) {
			if (!registry.containsKey(id)) registry.put(id, null);
		}
		public static List<Shader.Data> get(Identifier id) {
			return exists(id) ? registry.get(id) : null;
		}
		public static boolean exists(Identifier id) {
			return registry.containsKey(id);
		}
		public static void modify(Identifier id, List<Shader.Data> shaders) {
			registry.replace(id, shaders);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
		public static class Shaders {
			// Using these functions is optional, but makes it easier for mod developers to add shaders to their shader list.
			public static boolean register(Identifier registryId, Identifier shaderId, Shader shader) {
				if (ShaderRender.exists(registryId)) {
					List<Shader.Data> shaders = ShaderRender.get(registryId);
					if (shaders == null) shaders = new ArrayList<>();
					for (Shader.Data data : shaders) {
						if (data.id().equals(shaderId)) {
							return false;
						}
					}
					shaders.add(new Shader.Data(shaderId, shader));
					ShaderRender.modify(registryId, shaders);
					return true;
				}
				return false;
			}
			public static Shader.Data get(Identifier registryId, Identifier shaderId) {
				if (ShaderRender.exists(registryId)) {
					List<Shader.Data> shaders = ShaderRender.get(registryId);
					if (shaders == null) shaders = new ArrayList<>();
					for (Shader.Data data : shaders) {
						if (data.id().equals(shaderId)) {
							return data;
						}
					}
				}
				return null;
			}
			public static boolean modify(Identifier registryId, Identifier shaderId, Shader shader) {
				try {
					List<Shader.Data> shaders = ShaderRender.get(registryId);
					if (shaders != null) {
						for (Shader.Data data : shaders) {
							if (data.id().equals(shaderId)) {
								shaders.set(shaders.indexOf(data), new Shader.Data(shaderId, shader));
								break;
							}
						}
					}
					ShaderRender.modify(registryId, shaders);
					return true;
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set shader: {}:{}: {}", registryId, shaderId, error));
				}
				return false;
			}
			public static boolean set(Identifier registryId, Identifier shaderId, Shader shader) {
				try {
					if (!ShaderRender.exists(registryId)) ShaderRender.register(registryId, new ArrayList<>());
					return !exists(registryId, shaderId) ? register(registryId, shaderId, shader) : modify(registryId, shaderId, shader);
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set shader: {}:{}: {}", registryId, shaderId, error));
				}
				return false;
			}
			public static boolean exists(Identifier id, Identifier shaderId) {
				if (ShaderRender.exists(id)) {
					List<Shader.Data> shaders = ShaderRender.get(id);
					if (shaders != null) {
						for (Shader.Data data : shaders) {
							if (data.id().equals(shaderId)) return true;
						}
					}
				} return false;
			}
			public static boolean remove(Identifier id, Identifier shaderId) {
				List<Shader.Data> shaders = ShaderRender.get(id);
				if (shaders != null) {
					if (shaders.removeIf(shaderData -> shaderData.id().equals(shaderId))) {
						ShaderRender.modify(id, shaders);
						return true;
					}
				}
				return false;
			}
		}
		public static boolean remove(Identifier id, Shader.Data shader) {
			List<Shader.Data> shaders = ShaderRender.get(id);
			if (shaders != null) return shaders.remove(shader);
			return false;
		}
	}
}
