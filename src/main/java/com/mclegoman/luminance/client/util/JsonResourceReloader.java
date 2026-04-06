/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonResourceReloader extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
	private final Gson gson;
	private final String resourceLocation;
	public JsonResourceReloader(Gson gson, String resourceLocation) {
		this.gson = gson;
		this.resourceLocation = resourceLocation;
	}
	protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		Map<Identifier, JsonElement> map = new HashMap<>();
		load(resourceManager, this.resourceLocation, this.gson, map);
		return map;
	}
	public static void load(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results) {
		FileToIdConverter resourceFinder = FileToIdConverter.json(dataType);
		for (Map.Entry<Identifier, Resource> identifierResourceEntry : resourceFinder.listMatchingResources(manager).entrySet()) {
			Identifier resourceEntryKey = identifierResourceEntry.getKey();
			Identifier resourceId = resourceFinder.fileToId(resourceEntryKey);
			try {
				Reader reader = identifierResourceEntry.getValue().openAsReader();
				try {
					JsonElement jsonElement = results.put(resourceId, GsonHelper.fromJson(gson, reader, JsonElement.class));
					if (jsonElement != null) throw new IllegalStateException("Duplicate data file ignored with ID " + resourceId);
				} catch (Throwable throwable) {
                    try {
                        reader.close();
                    } catch (Throwable var12) {
                        throwable.addSuppressed(var12);
                    }
                    throw throwable;
				}
				reader.close();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Couldn't parse data file {} from {}: {}", resourceId, resourceEntryKey, error);
			}
		}

	}
}
