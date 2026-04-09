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
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.util.JsonResourceReloader;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.IdentifierHelper;
import com.mclegoman.luminance.common.util.LogType;
import net.fabricmc.fabric.impl.resource.FabricResourceReloader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShaderReloader extends JsonResourceReloader implements FabricResourceReloader {
	protected static boolean isReloading;
	public static final String resourceLocation = "luminance";

	public ShaderReloader() {
		super(new Gson(), resourceLocation);
	}

	private void reset() {
		SpectatorHandler.clearActive();
		Shaders.registries.clear();
		Events.OnShaderDataReset.registry.forEach((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute OnShaderDataReset event with id: {}:{}:", id, error);
			}
		});
	}

	private ShaderRegistryEntry getShaderData(Identifier id, boolean fallbackWhenOverUi, boolean fallbackWhenUnderUi, boolean photosensitive, JsonObject custom) {
		return ShaderRegistryEntry.builder(id).fallbackWhenOverUi(fallbackWhenOverUi).fallbackWhenUnderUi(fallbackWhenUnderUi).photosensitive(photosensitive).custom(custom).build();
	}

	private void add(List<Identifier> registries, ShaderRegistryEntry shaderData, ResourceManager manager) {
		try {
			manager.getResourceOrThrow(shaderData.getPostEffect(true));
			boolean alreadyRegistered = false;
			for (Identifier registry : registries) {
				for (ShaderRegistryEntry data : Shaders.getRegistry(registry)) {
					if (data.getID().equals(shaderData.getID())) {
						alreadyRegistered = true;
						Data.getVersion().sendToLog(LogType.WARN, "Failed to add \"{}\" shader to \"{}\" registry: This shader has already been registered!", shaderData.getID(), registry.toString());
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
		for (JsonElement registry : input.asList()) output.add(Identifier.parse(registry.getAsString()));
		return output;
	}

	@Override
	public void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
		try {
			isReloading = true;
			reset();
			prepared.forEach((identifier, jsonElement) -> {
				try {
					JsonObject reader = jsonElement.getAsJsonObject();

					if (Shaders.preventRegister(reader, identifier, "shader")) return;

					Identifier post_effect = IdentifierHelper.identifierFromString(GsonHelper.getAsString(reader, "post_effect", identifier.getNamespace() + ":" + identifier.getPath()));
					boolean enabled = GsonHelper.getAsBoolean(reader, "enabled", true);
					boolean fallbackWhenOverUi = GsonHelper.getAsBoolean(reader, "fallback_when_over_ui", false);
					boolean fallbackWhenUnderUi = GsonHelper.getAsBoolean(reader, "fallback_when_under_ui", false);
					boolean photosensitive = GsonHelper.getAsBoolean(reader, "photosensitive", false);
					JsonObject customData = GsonHelper.getAsJsonObject(reader, "custom", new JsonObject());
					JsonArray registries = GsonHelper.getAsJsonArray(reader, "registries", new JsonArray());
					ShaderRegistryEntry shaderData = getShaderData(post_effect, fallbackWhenOverUi, fallbackWhenUnderUi, photosensitive, customData);

					List<Identifier> registryList = getRegistries(registries);
					// If the registries are empty, we add the default registry.
					if (registries.isEmpty()) registryList.add(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "main"));

					if (enabled) {
						add(registryList, shaderData, manager);
						Events.OnShaderDataRegistered.registry.forEach((id, runnable) -> {
							try {
								runnable.run(shaderData, registryList);
							} catch (Exception error) {
								Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute OnShaderDataRegistered event with id: {}:{}:", id, error);
							}
						});
					} else {
						remove(registryList, shaderData);
						Events.OnShaderDataRemoved.registry.forEach((id, runnable) -> {
							try {
								runnable.run(shaderData, registryList);
							} catch (Exception error) {
								Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute OnShaderDataRemoved event with id: {}:{}:", id, error);
							}
						});
					}
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, "Failed to load luminance shader: {}", error);
				}
			});

			Events.AfterShaderDataRegistered.registry.forEach((id, runnable) -> {
				try {
					runnable.run();
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterShaderDataRegistered event with id: {}:{}:", id, error);
				}
			});

			Events.ShaderRender.registry.forEach((id, shaderRenderData) -> {
				if (shaderRenderData != null) {
					List<Shader.Data> shaders = shaderRenderData.shaders();
					if (shaders != null) shaders.forEach(shader -> {
						try {
							if (shader.shader() != null) shader.shader().reload();
						} catch (Exception error) {
							Data.getVersion().sendToLog(LogType.ERROR, "Failed to reload shader with id: {}:{}:", id, error);
						}
					});
				}
			});

			isReloading = false;
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to apply shaders dataloader: {}", error);
		}

		Debug.applyDebugShader();
	}

	@Override
	public @NonNull Identifier fabric$getId() {
		return Data.idOf(resourceLocation);
	}
}