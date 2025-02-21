/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.JsonDataLoader;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.IdentifierHelper;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShaderDataloader extends JsonDataLoader {
	protected static boolean isReloading;
	public static final String resourceLocation = "luminance";
	public ShaderDataloader() {
		super(new Gson(), resourceLocation);
	}
	private void reset() {
		Shaders.registries.clear();
		Events.OnShaderDataReset.registry.forEach((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute OnShaderDataReset event with id: {}:{}:", id, error));
			}
		});
	}
	private ShaderRegistryEntry getShaderData(Identifier id, boolean translatable, boolean description, boolean disableGameRendertype, JsonObject custom) {
		return ShaderRegistryEntry.builder(id).translatable(translatable).hasDescription(description).disableGameRendertype(disableGameRendertype).custom(custom).build();
	}
	private void add(List<Identifier> registries, ShaderRegistryEntry shaderData, ResourceManager manager) {
		try {
			manager.getResourceOrThrow(shaderData.getPostEffect(true));
			boolean alreadyRegistered = false;
			// If the registries are empty, we add the default registry.
			if (registries.isEmpty()) registries.add(Identifier.of(Data.getVersion().getID(), "main"));
			for (Identifier registry : registries) {
				for (ShaderRegistryEntry data : Shaders.getRegistry(registry)) {
					if (data.getID().equals(shaderData.getID())) {
						alreadyRegistered = true;
						Data.getVersion().sendToLog(LogType.WARN, Translation.getString("Failed to add \"{}\" shader to \"{}\" registry: This shader has already been registered!", shaderData.getID(), registry.toString()));
						break;
					}
				}
				if (!alreadyRegistered) Shaders.getRegistry(registry).add(shaderData);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.WARN, "Failed to add shader to registry: " + error);
		}
	}
	private void remove(List<Identifier> registries, ShaderRegistryEntry shaderData) {
		for (Identifier registry : registries) Shaders.getRegistry(registry).removeIf((shader) -> (shader.getID().equals(shaderData.getID())));
	}
	private List<Identifier> getRegistries(JsonArray input) {
		List<Identifier> output = new ArrayList<>();
		for (JsonElement registry : input.asList()) output.add(Identifier.of(registry.getAsString()));
		return output;
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
					boolean description = JsonHelper.getBoolean(reader, "description", false);
					boolean disableGameRenderType = JsonHelper.hasBoolean(reader, "disable_screen_mode") ? JsonHelper.getBoolean(reader, "disable_screen_mode") : JsonHelper.getBoolean(reader, "disable_game_rendertype", false);
					JsonObject customData = JsonHelper.getObject(reader, "custom", new JsonObject());
					JsonArray registries = JsonHelper.getArray(reader, "registries", new JsonArray());
					ShaderRegistryEntry shaderData = getShaderData(post_effect, translatable, description, disableGameRenderType, customData);
					List<Identifier> registryList = getRegistries(registries);
					if (enabled) {
						add(registryList, shaderData, manager);
						Events.OnShaderDataRegistered.registry.forEach((id, runnable) -> {
							try {
								runnable.run(shaderData, registryList);
							} catch (Exception error) {
								Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute OnShaderDataRegistered event with id: {}:{}:", id, error));
							}
						});
					} else {
						remove(registryList, shaderData);
						Events.OnShaderDataRemoved.registry.forEach((id, runnable) -> {
							try {
								runnable.run(shaderData, registryList);
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
		Shaders.applyDebugShader();
	}
}