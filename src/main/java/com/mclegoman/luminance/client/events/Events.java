/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.LuminanceIdentifier;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events {
    public static class BeforeInGameHudRender {
        public static final Map<Identifier, Runnables.InGameHudRender> registry = new HashMap<>();
        public static void register(Identifier id, Runnables.InGameHudRender runnable) {
            if (!registry.containsKey(id)) registry.put(id, runnable);
        }
        public static Runnables.InGameHudRender get(Identifier id) {
            return registry.get(id);
        }
        public static void modify(Identifier id, Runnables.InGameHudRender runnable) {
            registry.replace(id, runnable);
        }
        public static void remove(Identifier id) {
            registry.remove(id);
        }
    }
    public static class AfterInGameHudRender {
        public static final Map<Identifier, Runnables.InGameHudRender> registry = new HashMap<>();
        public static void register(Identifier id, Runnables.InGameHudRender runnable) {
            if (!registry.containsKey(id)) registry.put(id, runnable);
        }
        public static Runnables.InGameHudRender get(Identifier id) {
            return registry.get(id);
        }
        public static void modify(Identifier id, Runnables.InGameHudRender runnable) {
            registry.replace(id, runnable);
        }
        public static void remove(Identifier id) {
            registry.remove(id);
        }
    }
	public static class OnShaderDataReset {
		public static final Map<Identifier, Runnable> registry = new HashMap<>();
		public static void register(Identifier id, Runnable runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnable get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnable runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class OnShaderDataRegistered {
		public static final Map<Identifier, Runnables.ShaderData> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.ShaderData runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.ShaderData get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.ShaderData runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class OnShaderDataRemoved {
		public static final Map<Identifier, Runnables.ShaderData> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.ShaderData runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.ShaderData get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.ShaderData runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class AfterShaderDataRegistered {
		public static final Map<Identifier, Runnable> registry = new HashMap<>();
		public static void register(Identifier id, Runnable runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnable get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnable runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class BeforeWorldRender {
		public static final Map<Identifier, Runnable> registry = new HashMap<>();
		public static void register(Identifier id, Runnable runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnable get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnable runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class AfterWeatherRender {
		public static final Map<Identifier, Runnables.WorldRender> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.WorldRender runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.WorldRender get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.WorldRender runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class AfterWorldRender {
		public static final Map<Identifier, Runnables.GameRender> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.GameRender runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.GameRender get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.GameRender runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class AfterHandRender {
		public static final Map<Identifier, Runnables.GameRender> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.GameRender runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.GameRender get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.GameRender runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class BeforeGameRender {
		public static final Map<Identifier, Runnable> registry = new HashMap<>();
		public static void register(Identifier id, Runnable runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnable get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnable runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class AfterGameRender {
		public static final Map<Identifier, Runnables.GameRender> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.GameRender runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.GameRender get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.GameRender runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class OnResized {
		public static final Map<Identifier, Runnables.OnResized> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.OnResized runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.OnResized get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.OnResized runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class BeforeShaderRender {
		public static final Map<Identifier, Runnables.Shader> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.Shader runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.Shader get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.Shader runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class AfterShaderRender {
		public static final Map<Identifier, Runnables.Shader> registry = new HashMap<>();
		public static void register(Identifier id, Runnables.Shader runnable) {
			if (!registry.containsKey(id)) registry.put(id, runnable);
		}
		public static Runnables.Shader get(Identifier id) {
			return registry.get(id);
		}
		public static void modify(Identifier id, Runnables.Shader runnable) {
			registry.replace(id, runnable);
		}
		public static void remove(Identifier id) {
			registry.remove(id);
		}
	}
	public static class ShaderUniform {
		public static final Map<LuminanceIdentifier, Uniform> registry = new HashMap<>();
		public static void register(LuminanceIdentifier id, Uniform uniform) {
			if (!registry.containsKey(id)) registry.put(id, uniform);
		}
		public static void modify(LuminanceIdentifier id, Uniform uniform) {
			if (registry.containsKey(id)) registry.replace(id, uniform);
		}
		public static void remove(LuminanceIdentifier id) {
			registry.remove(id);
		}
	}
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
					Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to set shader: {}:{}: {}", registryId, shaderId, error));
				}
				return false;
			}
			public static boolean set(Identifier registryId, Identifier shaderId, Shader shader) {
				try {
					if (!ShaderRender.exists(registryId)) ShaderRender.register(registryId, new ArrayList<>());
					return !exists(registryId, shaderId) ? register(registryId, shaderId, shader) : modify(registryId, shaderId, shader);
				} catch (Exception error) {
					Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to set shader: {}:{}: {}", registryId, shaderId, error));
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
