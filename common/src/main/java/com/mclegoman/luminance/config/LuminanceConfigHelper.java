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
import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.implementor_api.ConfigFactory;

import java.io.File;
import java.nio.file.Path;

public class LuminanceConfigHelper {
	private static ConfigEnvironment tomlConfigEnvironment;
	private static ConfigEnvironment propertiesConfigEnvironment;
	private static ConfigEnvironment jsonConfigEnvironment;
	public static <C extends ReflectiveConfig> C register(SerializerType type, String namespace, String id, Class<C> config) {
		return ConfigFactory.create(getConfigEnvironment(type), namespace, id, Path.of(""), builder -> {}, config, builder -> {});
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
		if (type == SerializerType.JSON5) {
			if (jsonConfigEnvironment == null) {
				jsonConfigEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "toml", TomlSerializer.INSTANCE);
				jsonConfigEnvironment.registerSerializer(TomlSerializer.INSTANCE);
			}
			return jsonConfigEnvironment;
		} else if (type == SerializerType.PROPERTIES) {
			if (propertiesConfigEnvironment == null) {
				LuminanceSerializer serializer = new LuminanceSerializer("properties");
				propertiesConfigEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "properties", serializer);
				propertiesConfigEnvironment.registerSerializer(serializer);
			}
			return propertiesConfigEnvironment;
		} else {
			if (tomlConfigEnvironment == null) {
				tomlConfigEnvironment = new ConfigEnvironment(new File(ClientData.minecraft.runDirectory, "config").toPath(), "toml", TomlSerializer.INSTANCE);
				tomlConfigEnvironment.registerSerializer(TomlSerializer.INSTANCE);
			}
			return tomlConfigEnvironment;
		}
	}
	public enum SerializerType {
		TOML,
		PROPERTIES,
		JSON5
	}
}
