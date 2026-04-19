/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces.internal;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InternalUniformValueInterface {
    Optional<String> luminance$getName();
    void luminance$setName(String name);

    Optional<List<String>> luminance$getOverride();
    void luminance$setOverride(List<String> overrides);

    Optional<Map<String, List<Object>>> luminance$getConfig();
    void luminance$setConfig(Map<String, List<Object>> config);

    int luminance$getLength();
    ImmutableList<Number> luminance$getValue();
}
