package com.mclegoman.luminance.client.shaders.uniforms.config;

import java.util.List;

public interface UniformConfig {
    Number getOrDefault(String name, int index, Number defaultValue);
    List<Object> get(String name);
}
