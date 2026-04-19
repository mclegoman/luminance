/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.google.common.collect.ImmutableMap;
import com.mclegoman.luminance.client.shaders.CustomPassData;
import com.mclegoman.luminance.client.shaders.UniformBlock;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public interface PostPassInterface {
    RenderPipeline luminance$getPipeline();

    ImmutableMap<String, UniformBlock> luminance$getUniformBlocks();

    UniformBlock luminance$getUniformBlock(String block);

    Identifier luminance$getOutputTarget();
    
    CustomPassData luminance$putCustomData(Identifier identifier, CustomPassData data);

    Optional<CustomPassData> luminance$getCustomData(Identifier identifier);

    boolean luminance$usesDepth();

    boolean luminance$usesImprovedTransparency();
}
