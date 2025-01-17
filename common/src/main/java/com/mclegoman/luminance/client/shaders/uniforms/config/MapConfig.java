package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MapConfig implements UniformConfig {
    private final Map<String, List<Object>> config;

    public MapConfig(List<ConfigData> configValues) {
        this.config = new HashMap<>(configValues.size());
        for (ConfigData configData : configValues) {
            this.config.put(configData.name(), configData.objects());
        }
    }

    @Override
    public Set<String> getNames() {
        return config.keySet();
    }

    @Override @Nullable
    public List<Object> getObjects(String name) {
        return config.get(name);
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
}
