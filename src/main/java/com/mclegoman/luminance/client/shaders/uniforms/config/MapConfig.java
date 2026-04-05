/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public record MapConfig(Map<String, List<Object>> config) implements UniformConfig {
    public MapConfig(Map<String, List<Object>> config) {
        this.config = new HashMap<>(config);
    }

    @Override
    public Set<String> getNames() {
        return config.keySet();
    }

    @Override
    @Nullable
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

    @Override
    public UniformConfig copy() {
        // create new arraylists, so that theyre mutable
        MapConfig mapConfig = new MapConfig(Map.of());
        config.forEach((name, objects) -> mapConfig.config.put(name, new ArrayList<>(objects)));
        return mapConfig;
    }

    public void mergeWithConfig(UniformConfig newConfig) {
        if (newConfig == null) return;

        for (String s : newConfig.getNames()) {
            List<Object> objects = newConfig.getObjects(s);
            if (objects != null) {
                config.putIfAbsent(s, objects);
            }
        }
    }
}
