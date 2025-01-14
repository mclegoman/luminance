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
        super.onRegister(fullName);
        assert parent != null;
        delta = new UniformValue(parent.getLength());
    }

    @Override
    public void beforeParentCalculation(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        delta = parent.get(config, shaderTime).copyTo(delta);
    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        delta.subtract(parent.get(config, shaderTime));
    }

    @Override
    public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
        return delta;
    }
}
