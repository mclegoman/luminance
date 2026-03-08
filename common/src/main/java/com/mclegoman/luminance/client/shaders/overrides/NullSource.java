package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

// error source for when entering a string that isnt an identifier or float
public class NullSource implements OverrideSource {
    public String string;

    public NullSource(String string) {
        this.string = string;
    }

    @Override
    public Float get(UniformConfig config, ShaderTime shaderTime) {
        return null;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public UniformConfig getTemplateConfig() {
        return EmptyConfig.INSTANCE;
    }
}
