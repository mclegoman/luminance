/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostEffectPassInterface {
    String luminance$getID();

    List<PostEffectPipeline.Uniform> luminance$getUniforms();

    UniformOverride luminance$getUniformOverride(String uniform);

    UniformOverride luminance$addUniformOverride(String uniform, UniformOverride override);

    UniformOverride luminance$removeUniformOverride(String uniform);

    Map<String, UniformConfig> luminance$getUniformConfigs();

    Identifier luminance$getOutputTarget();

    void luminance$setForceVisit(boolean to);

    Object luminance$putCustomData(Identifier identifier, Object object);

    Optional<Object> luminance$getCustomData(Identifier identifier);
}
