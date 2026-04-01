/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces.pipeline;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;

import java.util.List;
import java.util.Optional;

public interface UniformValueInterface {
    Optional<String> luminance$getName();
    void luminance$setName(String name);

    Optional<List<String>> luminance$getOverride();
    void luminance$setOverride(List<String> overrides);

    Optional<List<ConfigData>> luminance$getConfig();
    void luminance$setConfig(List<ConfigData> config);

    int luminance$getLength();
    ImmutableList<Number> luminance$getValue();
}
