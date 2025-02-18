/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

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
    public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        prev = parent.getCache(config, shaderTime).copyTo(prev);
    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {

    }

    @Override
    public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
        return prev;
    }

    @Override
    public UniformConfig getDefaultConfig() {
        return EmptyConfig.INSTANCE;
    }
}
