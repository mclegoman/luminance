/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.UniformData;
import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostEffectPassInterface {
    String luminance$getID();

    ImmutableList<@NotNull UniformData> luminance$getUniformData(String block);

    Identifier luminance$getOutputTarget();

    void luminance$setForceVisit(boolean to);

    CustomPassData luminance$putCustomData(Identifier identifier, CustomPassData data);

    Optional<CustomPassData> luminance$getCustomData(Identifier identifier);

    boolean luminance$usesDepth();

    PostEffectPass luminance$copy();
}
