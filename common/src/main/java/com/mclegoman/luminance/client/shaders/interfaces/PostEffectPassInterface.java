/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.util.Identifier;

import java.util.List;

public interface PostEffectPassInterface {
    String luminance$getID();

    ShaderProgram luminance$getProgram();

    List<PostEffectPipeline.Uniform> luminance$getUniforms();

    UniformOverride luminance$getUniformOverride(String uniform);

    UniformOverride luminance$addUniformOverride(String uniform, UniformOverride override);

    UniformOverride luminance$removeUniformOverride(String uniform);

    Identifier luminance$getOutputTarget();

    void luminance$setForceVisit(boolean to);
}
