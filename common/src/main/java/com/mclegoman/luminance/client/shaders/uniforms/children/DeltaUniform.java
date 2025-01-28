/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

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
    public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        delta = parent.getCache(config, shaderTime).copyTo(delta);
    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        delta.subtract(parent.getCache(config, shaderTime));
    }

    @Override
    public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
        return delta;
    }

    @Override
    public UniformConfig getDefaultConfig() {
        return EmptyConfig.INSTANCE;
    }
}
