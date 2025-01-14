package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

public class DeltaUniform extends ChildUniform {
    protected UniformValue delta;

    public DeltaUniform() {
        super("delta");
    }

    @Override
    public void onRegister(String fullName) {
        assert parent != null;
        delta = new UniformValue(parent.getLength());
    }

    @Override
    public void preParentUpdate(ShaderTime shaderTime) {
        assert parent != null;
        delta = parent.get(UniformConfig.EMPTY, shaderTime).copyTo(delta);
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        assert parent != null;
        delta.subtract(parent.get(UniformConfig.EMPTY, shaderTime));
    }

    @Override
    public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
        return delta;
    }
}
