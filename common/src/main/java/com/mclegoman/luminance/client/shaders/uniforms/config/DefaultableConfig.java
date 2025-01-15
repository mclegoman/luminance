package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DefaultableConfig implements UniformConfig {
    public UniformConfig uniformConfig;
    public UniformConfig defaultConfig;

    public DefaultableConfig(UniformConfig uniformConfig, UniformConfig defaultConfig) {
        this.uniformConfig = uniformConfig;
        this.defaultConfig = defaultConfig;
    }

    @Override
    public Optional<Number> getNumber(String name, int index) {
        Optional<Number> number = uniformConfig.getNumber(name, index);
        if (number.isEmpty()) {
            return defaultConfig.getNumber(name, index);
        }
        return number;
    }

    @Override @Nullable
    public List<Object> get(String name) {
        List<Object> list = uniformConfig.get(name);
        if (list == null) {
            return defaultConfig.get(name);
        }
        return list;
    }
}
