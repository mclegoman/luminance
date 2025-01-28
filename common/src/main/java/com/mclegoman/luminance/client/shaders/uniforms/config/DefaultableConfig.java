/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DefaultableConfig implements UniformConfig {
    public UniformConfig uniformConfig;
    public UniformConfig defaultConfig;

    public DefaultableConfig(UniformConfig uniformConfig, UniformConfig defaultConfig) {
        this.uniformConfig = uniformConfig;
        this.defaultConfig = defaultConfig;
    }

    @Override
    public Set<String> getNames() {
        Set<String> names = new HashSet<>(uniformConfig.getNames());
        names.addAll(defaultConfig.getNames());
        return names;
    }

    @Override @Nullable
    public List<Object> getObjects(String name) {
        List<Object> list = uniformConfig.getObjects(name);
        if (list == null) {
            return defaultConfig.getObjects(name);
        }
        return list;
    }

    @Override
    public Optional<Number> getNumber(String name, int index) {
        Optional<Number> number = uniformConfig.getNumber(name, index);
        if (number.isEmpty()) {
            return defaultConfig.getNumber(name, index);
        }
        return number;
    }
}
