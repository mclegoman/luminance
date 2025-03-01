/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> {
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
			Identifier resourceEntryKey = identifierResourceEntry.getKey();
			Identifier resourceId = resourceFinder.toResourceId(resourceEntryKey);
			try {
				Reader reader = identifierResourceEntry.getValue().getReader();
				try {
					JsonElement jsonElement = results.put(resourceId, JsonHelper.deserialize(gson, reader, JsonElement.class));
					if (jsonElement != null) throw new IllegalStateException("Duplicate data file ignored with ID " + resourceId);
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
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Couldn't parse data file {} from {}: {}", resourceId, resourceEntryKey, error));
			}
		}

	}
}
