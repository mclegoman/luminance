package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapConfig implements UniformConfig {
    private final Map<String, List<Object>> config;

    public MapConfig(List<ConfigData> configValues) {
        this.config = new HashMap<>(configValues.size());
        for (ConfigData configData : configValues) {
            this.config.put(configData.name(), configData.objects());
        }
    }

    @Override
    public Optional<Number> getNumber(String name, int index) {
        List<Object> objects = config.get(name);
        if (objects == null || objects.size() <= index) {
            return Optional.empty();
        }

        Object object = objects.get(index);
        if (object instanceof Number number) {
            return Optional.of(number);
        }

        return Optional.empty();
    }

    @Override @Nullable
    public List<Object> get(String name) {
        return config.get(name);
    }
}
