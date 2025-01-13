package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;

public class PrevUniform extends TreeUniform {
    protected float current;
    protected float prev;

    public PrevUniform() {
        super("prev");
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        if (parent == null) {
            return;
        }

        prev = current;
        current = parent.get(UniformConfig.EMPTY, shaderTime);
    }

    @Override
    public float get(UniformConfig config, ShaderTime shaderTime) {
        return prev;
    }
}
