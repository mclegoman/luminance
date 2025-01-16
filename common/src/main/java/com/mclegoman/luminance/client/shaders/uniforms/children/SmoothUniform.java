package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

public class SmoothUniform extends ChildUniform {
    protected UniformValue smooth;

    public SmoothUniform() {
        super("smooth");
    }

    @Override
    public void onRegister(String fullName) {
        super.onRegister(fullName);
        assert parent != null;
        smooth = new UniformValue(parent.getLength());
    }

    @Override
    public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {

    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        UniformValue uniformValue = parent.getCache(config, shaderTime);
        smooth = uniformValue.copyTo(smooth);
        smooth.lerp(uniformValue, shaderTime.getDeltaTime());
    }

    @Override
    public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
        return smooth;
    }

    @Override
    public UniformConfig getDefaultConfig() {
        return EmptyConfig.INSTANCE;
    }
}
