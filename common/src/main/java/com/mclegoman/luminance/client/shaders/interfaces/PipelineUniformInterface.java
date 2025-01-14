/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import java.util.List;
import java.util.Optional;

public interface PipelineUniformInterface {
    Optional<List<String>> luminance$getOverride();
    void luminance$setOverride(List<String> overrides);
}
