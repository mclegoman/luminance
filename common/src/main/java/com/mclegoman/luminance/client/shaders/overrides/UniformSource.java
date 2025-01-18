/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

public class UniformSource implements OverrideSource {
    protected final String name;

    @Nullable
    protected Uniform uniform = null;

    public UniformSource(String name) {
        this.name = name;
    }

    @Override
    public Float get(UniformConfig config, ShaderTime shaderTime) {
        Uniform uniform = getUniform();
        if (uniform == null) return null;
        return uniform.get(config, shaderTime).values.getFirst();
    }

    @Override
    public String getString() {
        return name;
    }

    @Override
    public UniformConfig getTemplateConfig() {
        Uniform uniform = getUniform();
        if (uniform == null) return EmptyConfig.INSTANCE;
        return uniform.getDefaultConfig();
    }

    public Uniform getUniform() {
        if (uniform == null) {
            uniform = Events.ShaderUniform.registry.get(name);
        }

        return uniform;
    }
}
