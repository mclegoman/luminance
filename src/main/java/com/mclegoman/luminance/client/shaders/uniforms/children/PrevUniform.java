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
        assert parent != null;
        prev = new UniformValue(parent.getLength());
    }

    @Override
    public void preParentUpdate(ShaderTime shaderTime) {
        assert parent != null;
        prev = parent.get(UniformConfig.EMPTY, shaderTime).copyTo(prev);
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {

    }

    @Override
    public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
        return prev;
    }
}
