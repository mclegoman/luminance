/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.config;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.config.serializers.LuminanceSerializer;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.serializers.Json5Serializer;
import org.quiltmc.config.api.serializers.TomlSerializer;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.impl.ConfigImpl;
import org.quiltmc.config.implementor_api.ConfigEnvironment;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LuminanceConfigHelper {
	private static final Map<SerializerType, ConfigEnvironment> configEnvironments = new HashMap<>();
	public static <C extends ReflectiveConfig> C register(SerializerType type, String namespace, String id, Class<C> config) {
		return ConfigImpl.createReflective(getConfigEnvironment(type), namespace, id, Path.of(""), builder -> {}, config, builder -> {});
	}
	public static <C extends ReflectiveConfig> void reset(C config) {
		reset(config, true);
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <C extends ReflectiveConfig> void reset(C config, boolean save) {
		for (TrackedValue value : config.values()) value.setValue(value.getDefaultValue(), false);
		if (save) config.save();
	}
	public static ConfigEnvironment getConfigEnvironment(SerializerType type) {
		return configEnvironments.get(type);
	}
	public enum SerializerType {
		TOML,
		PROPERTIES,
		JSON5
	}
	static {
		// Register JSON5 config serializer
		ConfigEnvironment jsonConfigEnvironment;
		jsonConfigEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "json", Json5Serializer.INSTANCE);
		jsonConfigEnvironment.registerSerializer(Json5Serializer.INSTANCE);
		configEnvironments.put(SerializerType.JSON5, jsonConfigEnvironment);
		// Register PROPERTIES config serializer
		ConfigEnvironment propertiesConfigEnvironment;
		propertiesConfigEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "properties", new LuminanceSerializer("properties"));
		propertiesConfigEnvironment.registerSerializer(new LuminanceSerializer("properties"));
		configEnvironments.put(SerializerType.PROPERTIES, propertiesConfigEnvironment);
		// Register TOML config serializer
		ConfigEnvironment tomlConfigEnvironment;
		tomlConfigEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "toml", TomlSerializer.INSTANCE);
		tomlConfigEnvironment.registerSerializer(TomlSerializer.INSTANCE);
		configEnvironments.put(SerializerType.TOML, tomlConfigEnvironment);
	}
}
