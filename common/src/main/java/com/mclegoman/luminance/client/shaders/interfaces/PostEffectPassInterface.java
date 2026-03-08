/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.UniformData;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface PostEffectPassInterface {
    String luminance$getID();

    // TODO: this should perhaps just be an array
    ImmutableList<@NotNull UniformData> luminance$getUniformData(String block);

    // TODO: methods for getting uniform names

    Identifier luminance$getOutputTarget();

    void luminance$setForceVisit(boolean to);

    CustomPassData luminance$putCustomData(Identifier identifier, CustomPassData data);

    Optional<CustomPassData> luminance$getCustomData(Identifier identifier);

    boolean luminance$usesDepth();

    PostEffectPass luminance$copy();
}
