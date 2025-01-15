package com.mclegoman.luminance.client.shaders.uniforms.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapConfig implements UniformConfig {
    private final Map<String, List<Object>> config;

    public MapConfig(List<ConfigData> configValues) {
        this.config = new HashMap<>(configValues.size());
        for (ConfigData configData : configValues) {
            this.config.put(configData.name(), configData.objects());
        }
    }

    public Number getOrDefault(String name, int index, Number defaultValue) {
        List<Object> objects = config.get(name);
        if (objects == null || objects.size() <= index) {
            return defaultValue;
        }

        Object object = objects.get(index);
        if (object instanceof Number number) {
            return number;
        }

        return defaultValue;
    }

    public List<Object> get(String name) {
        return config.get(name);
    }

}
