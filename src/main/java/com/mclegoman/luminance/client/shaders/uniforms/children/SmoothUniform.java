package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

public class SmoothUniform extends ChildUniform {
    protected UniformValue smooth;

    public SmoothUniform() {
        super("smooth");
    }

    @Override
    public void onRegister(String fullName) {
        assert parent != null;
        smooth = new UniformValue(parent.getLength());
    }

    @Override
    public void preParentUpdate(ShaderTime shaderTime) {

    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        assert parent != null;
        UniformValue uniformValue = parent.get(UniformConfig.EMPTY, shaderTime);
        smooth = uniformValue.copyTo(smooth);
        smooth.lerp(uniformValue, shaderTime.getDeltaTime());
    }

    @Override
    public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
        return smooth;
    }
}
