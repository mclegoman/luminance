package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface UniformConfig {
    Optional<Number> getNumber(String name, int index);
    @Nullable
    List<Object> get(String name);
}
