/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EmptyConfig implements UniformConfig {
    public static final UniformConfig INSTANCE = new EmptyConfig();

    @Override
    public Set<String> getNames() {
        return Set.of();
    }

    @Override @Nullable
    public List<Object> getObjects(String name) {
        return null;
    }

    @Override
    public Optional<Number> getNumber(String name, int index) {
        return Optional.empty();
    }
}
