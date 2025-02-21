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
		ConfigEnvironment configEnvironment;
		// Register JSON5 config serializer
		configEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "toml", TomlSerializer.INSTANCE);
		configEnvironment.registerSerializer(TomlSerializer.INSTANCE);
		configEnvironments.put(SerializerType.JSON5, configEnvironment);
		// Register PROPERTIES config serializer
		LuminanceSerializer serializer = new LuminanceSerializer("properties");
		configEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "properties", serializer);
		configEnvironment.registerSerializer(serializer);
		configEnvironments.put(SerializerType.PROPERTIES, configEnvironment);
		// Register TOML config serializer
		configEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "toml", TomlSerializer.INSTANCE);
		configEnvironment.registerSerializer(TomlSerializer.INSTANCE);
		configEnvironments.put(SerializerType.TOML, configEnvironment);
	}
}
