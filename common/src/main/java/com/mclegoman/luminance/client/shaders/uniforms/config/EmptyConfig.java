package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EmptyConfig implements UniformConfig {
    public static final UniformConfig INSTANCE = new EmptyConfig();

    @Override
    public Optional<Number> getNumber(String name, int index) {
        return Optional.empty();
    }

    @Override @Nullable
    public List<Object> get(String name) {
        return List.of();
    }
}
