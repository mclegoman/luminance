/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

public class FixedValueSource implements OverrideSource {
    public float value;

    public FixedValueSource(float value) {
        this.value = value;
    }

    @Override
    public Float get(UniformConfig config, ShaderTime shaderTime) {
        return value;
    }

    @Override
    public String getString() {
        return Float.toString(value);
    }

    @Override
    public UniformConfig getTemplateConfig() {
        return EmptyConfig.INSTANCE;
    }
}
