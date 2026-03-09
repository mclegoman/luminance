/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.UniformInstance;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface PostEffectPassInterface {
    String luminance$getID();

    // this should perhaps just be an array? arrays are a little mean to generics though
    ImmutableList<@NotNull UniformInstance> luminance$getUniformInstances(String block);

    Identifier luminance$getOutputTarget();

    void luminance$setForceVisit(boolean to);

    CustomPassData luminance$putCustomData(Identifier identifier, CustomPassData data);

    Optional<CustomPassData> luminance$getCustomData(Identifier identifier);

    boolean luminance$usesDepth();
}
