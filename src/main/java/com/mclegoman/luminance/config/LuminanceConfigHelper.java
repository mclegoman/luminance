/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.config;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.serializers.TomlSerializer;
import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.implementor_api.ConfigFactory;

import java.nio.file.Path;

public class LuminanceConfigHelper {
	private static ConfigEnvironment tomlConfigEnvironment;
	private static ConfigEnvironment jsonConfigEnvironment;
	public static <C extends ReflectiveConfig> C register(SerializerType type, String namespace, String id, Class<C> config) {
		return ConfigFactory.create(getConfigEnvironment(type), namespace, id, Path.of(""), builder -> {}, config, builder -> {});
	}
	public static ConfigEnvironment getConfigEnvironment(SerializerType type) {
		if (type == SerializerType.JSON5) {
			if (jsonConfigEnvironment == null) {
				jsonConfigEnvironment = new ConfigEnvironment(FabricLoaderImpl.INSTANCE.getConfigDir(), "toml", TomlSerializer.INSTANCE);
				jsonConfigEnvironment.registerSerializer(TomlSerializer.INSTANCE);
			}
			return jsonConfigEnvironment;
		} else {
			if (tomlConfigEnvironment == null) {
				tomlConfigEnvironment = new ConfigEnvironment(FabricLoaderImpl.INSTANCE.getConfigDir(), "toml", TomlSerializer.INSTANCE);
				tomlConfigEnvironment.registerSerializer(TomlSerializer.INSTANCE);
			}
			return tomlConfigEnvironment;
		}
	}
	public enum SerializerType {
		TOML,
		JSON5
	}
}
