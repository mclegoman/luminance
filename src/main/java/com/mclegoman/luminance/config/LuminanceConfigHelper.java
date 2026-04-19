/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.config;

import com.mclegoman.luminance.config.serializers.LuminanceSerializer;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.implementor_api.ConfigEnvironment;

import java.nio.file.Path;

public class LuminanceConfigHelper {
	public static final LuminanceSerializer PROPERTIES_INSTANCE = new LuminanceSerializer("properties");

	public static ConfigEnvironment propertiesEnvironment(Path configPath) {
		return new ConfigEnvironment(configPath, "properties", PROPERTIES_INSTANCE);
	}

	public static <T extends ReflectiveConfig> T register(Path configPath, String familyId, String id, Class<T> configCreatorClass) {
		return ReflectiveConfig.create(propertiesEnvironment(configPath), familyId, id, configCreatorClass);
	}

	public static <C extends ReflectiveConfig> void reset(C config) {
		reset(config, true);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <C extends ReflectiveConfig> void reset(C config, boolean save) {
		for (TrackedValue value : config.values()) value.setValue(value.getDefaultValue(), false);
		if (save) config.save();
	}
}
