/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces.pipeline;

import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PipelineInterface {
    Optional<Map<Identifier, List<PostChainConfig.Pass>>> luminance$getCustomPasses();
    void luminance$setCustomPasses(Map<Identifier, List<PostChainConfig.Pass>> passes);
}
