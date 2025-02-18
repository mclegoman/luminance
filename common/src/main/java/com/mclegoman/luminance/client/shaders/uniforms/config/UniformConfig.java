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

public interface UniformConfig {
    Set<String> getNames();
    @Nullable
    List<Object> getObjects(String name);
    Optional<Number> getNumber(String name, int index);
}
