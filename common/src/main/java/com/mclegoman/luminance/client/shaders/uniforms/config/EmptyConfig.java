package com.mclegoman.luminance.client.shaders.uniforms.config;

import java.util.List;

public class EmptyConfig implements UniformConfig {
    public static final UniformConfig INSTANCE = new EmptyConfig();

    @Override
    public Number getOrDefault(String name, int index, Number defaultValue) {
        return defaultValue;
    }

    @Override
    public List<Object> get(String name) {
        return List.of();
    }
}
