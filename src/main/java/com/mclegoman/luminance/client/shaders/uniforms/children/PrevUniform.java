package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

public class PrevUniform extends ChildUniform {
    protected UniformValue prev;

    public PrevUniform() {
        super("prev");
    }

    @Override
    public void onRegister(String fullName) {
        super.onRegister(fullName);
        assert parent != null;
        prev = new UniformValue(parent.getLength());
    }

    @Override
    public void beforeParentCalculation(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        prev = parent.get(config, shaderTime).copyTo(prev);
    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {

    }

    @Override
    public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
        return prev;
    }
}
