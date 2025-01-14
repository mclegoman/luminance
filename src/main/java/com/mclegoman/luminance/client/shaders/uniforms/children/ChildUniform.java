package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

import java.util.Optional;

public abstract class ChildUniform extends TreeUniform {
    protected ChildUniform(String name) {
        super(name);
    }

    @Override
    public int getLength() {
        assert parent != null;
        return parent.getLength();
    }

    @Override
    public Optional<UniformValue> getMin() {
        assert parent != null;
        return parent.getMin();
    }

    @Override
    public Optional<UniformValue> getMax() {
        assert parent != null;
        return parent.getMax();
    }
}
