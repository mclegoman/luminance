package com.mclegoman.luminance.client.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Gson gson;
	private final String dataType;

	public JsonDataLoader(Gson gson, String dataType) {
		this.gson = gson;
		this.dataType = dataType;
	}

	protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, JsonElement> map = new HashMap<>();
		load(resourceManager, this.dataType, this.gson, map);
		return map;
	}

	public static void load(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results) {
		ResourceFinder resourceFinder = ResourceFinder.json(dataType);

		for (Map.Entry<Identifier, Resource> identifierResourceEntry : resourceFinder.findResources(manager).entrySet()) {
			Identifier identifier = identifierResourceEntry.getKey();
			Identifier identifier2 = resourceFinder.toResourceId(identifier);

			try {
				Reader reader = identifierResourceEntry.getValue().getReader();

				try {
					JsonElement jsonElement = JsonHelper.deserialize(gson, reader, JsonElement.class);
					JsonElement jsonElement2 = results.put(identifier2, jsonElement);
					if (jsonElement2 != null) {
						throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
					}
				} catch (Throwable throwable) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var12) {
							throwable.addSuppressed(var12);
						}
					}

					throw throwable;
				}

				reader.close();
			} catch (IllegalArgumentException | IOException | JsonParseException error) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, error);
			}
		}

	}
}
