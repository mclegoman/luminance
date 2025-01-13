package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;

import java.util.Optional;

public class DeltaUniform extends TreeUniform<Float, Float> {
    protected float current;
    protected float prev;

    public DeltaUniform() {
        super("delta");
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        prev = current;
        assert parent != null;
        current = parent.get(UniformConfig.EMPTY, shaderTime);
    }

    @Override
    public Float get(UniformConfig config, ShaderTime shaderTime) {
        return current-prev;
    }

    @Override
    public Optional<Float> getMin() {
        assert parent != null;
        return parent.getMin();
    }

    @Override
    public Optional<Float> getMax() {
        assert parent != null;
        return parent.getMax();
    }
}
