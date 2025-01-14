/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;

public class UniformSource implements OverrideSource {
    protected final String name;

    protected Uniform uniform = null;

    public final UniformConfig uniformConfig;

    public UniformSource(String name) {
        this.name = name;
        this.uniformConfig = UniformConfig.EMPTY;
    }

    @Override
    public Float get() {
        Uniform uniform = getUniform();
        if (uniform == null) return null;
        return uniform.get(uniformConfig, Uniforms.shaderTime).values.getFirst();
    }

    @Override
    public String getString() {
        return name;
    }

    public Uniform getUniform() {
        if (uniform == null) {
            uniform = Events.ShaderUniform.registry.get(name);
        }

        return uniform;
    }
}
