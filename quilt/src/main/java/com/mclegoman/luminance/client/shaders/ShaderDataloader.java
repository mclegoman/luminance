/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.JsonDataLoader;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.IdentifierHelper;
import com.mclegoman.luminance.common.util.LogType;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShaderDataloader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	protected static boolean isReloading;
	public static final String resourceLocation = "luminance";
	public ShaderDataloader() {
		super(new Gson(), resourceLocation);
	}
	private void reset() {
		Shaders.registry.clear();
		Events.OnShaderDataReset.registry.forEach((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute OnShaderDataReset event with id: {}:{}:", id, error));
			}
		});
	}
	private ShaderRegistry getShaderData(Identifier id, boolean translatable, boolean disableGameRendertype, JsonObject custom) {
		return ShaderRegistry.builder(id).translatable(translatable).disableGameRendertype(disableGameRendertype).custom(custom).build();
	}
	private void add(ShaderRegistry shaderData, ResourceManager manager) {
		try {
			manager.getResourceOrThrow(shaderData.getPostEffect(true));
			boolean alreadyRegistered = false;
			for (ShaderRegistry data : Shaders.registry) {
				if (data.getID().equals(shaderData.getID())) {
					alreadyRegistered = true;
					Data.getVersion().sendToLog(LogType.WARN, Translation.getString("Failed to add \"{}\" shader to registry: This shader has already been registered!", shaderData.getID()));
					break;
				}
			}
			if (!alreadyRegistered) Shaders.registry.add(shaderData);
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.WARN, "Failed to add shader to registry: " + error);
		}
	}
	private void remove(ShaderRegistry shaderData) {
		Shaders.registry.removeIf((shader) -> (shader.getID().equals(shaderData.getID())));
	}
	@Override
	public void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		try {
			isReloading = true;
			reset();
			prepared.forEach((identifier, jsonElement) -> {
				try {
					JsonObject reader = jsonElement.getAsJsonObject();
					Identifier post_effect = IdentifierHelper.identifierFromString(JsonHelper.getString(reader, "post_effect", identifier.getNamespace() + ":" + identifier.getPath()));
					boolean enabled = JsonHelper.getBoolean(reader, "enabled", true);
					boolean translatable = JsonHelper.getBoolean(reader, "translatable", false);
					boolean disableGameRenderType = JsonHelper.hasBoolean(reader, "disable_screen_mode") ? JsonHelper.getBoolean(reader, "disable_screen_mode") : JsonHelper.getBoolean(reader, "disable_game_rendertype", false);
					JsonObject customData = JsonHelper.getObject(reader, "customData", new JsonObject());
					ShaderRegistry shaderData = getShaderData(post_effect, translatable, disableGameRenderType, customData);
					if (enabled) {
						add(shaderData, manager);
						Events.OnShaderDataRegistered.registry.forEach((id, runnable) -> {
							try {
								runnable.run(shaderData);
							} catch (Exception error) {
								Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute OnShaderDataRegistered event with id: {}:{}:", id, error));
							}
						});
					} else {
						remove(shaderData);
						Events.OnShaderDataRemoved.registry.forEach((id, runnable) -> {
							try {
								runnable.run(shaderData);
							} catch (Exception error) {
								Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute OnShaderDataRemoved event with id: {}:{}:", id, error));
							}
						});
					}
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to load luminance shader: {}", error));
				}
			});
			Events.AfterShaderDataRegistered.registry.forEach((id, runnable) -> {
				try {
					runnable.run();
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterShaderDataRegistered event with id: {}:{}:", id, error));
				}
			});
			Events.ShaderRender.registry.forEach((id, shaders) -> {
				if (shaders != null) shaders.forEach(shader -> {
					try {
						if (shader.shader() != null) shader.shader().reload();
					} catch (Exception error) {
						Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to reload shader with id: {}:{}:", id, error));
					}
				});
			});
			isReloading = false;
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to apply shaders dataloader: {}", error));
		}
		if (ClientData.isDevelopment) {
			// Test Shader: remove/comment out when shader rendering is finished.
			Events.ShaderRender.register(Identifier.of(Data.getVersion().getID(), "debug"), new ArrayList<>());
			Events.ShaderRender.modify(Identifier.of(Data.getVersion().getID(), "debug"), List.of(new Shader.Data(Identifier.of(Data.getVersion().getID(), "debug"), new Shader(Shaders.get(Identifier.of("minecraft", "wobble")), () -> Debug.debugRenderType, () -> Debug.debugShader))));
			//Events.ShaderRender.modify(Identifier.of(Data.getVersion().getID(), "test"), List.of(new Shader.Data(Identifier.of(Data.getVersion().getID(), "test"), new Shader(Shaders.get(Identifier.of("minecraft", "phosphor")), () -> Debug.debugRenderType, () -> Debug.debugShader))));
		}
	}
	@Override
	public Identifier getFabricId() {
		return Identifier.of(Data.getVersion().getID(), resourceLocation);
	}
}