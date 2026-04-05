/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.mclegoman.luminance.client.shaders.UniformBlock;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public interface PostEffectPassInterface {
    String luminance$getID();

    UniformBlock luminance$getUniformInstances(String block);

    Identifier luminance$getOutputTarget();

    void luminance$setForceVisit(boolean to);

    CustomPassData luminance$putCustomData(Identifier identifier, CustomPassData data);

    Optional<CustomPassData> luminance$getCustomData(Identifier identifier);

    boolean luminance$usesDepth();
}
