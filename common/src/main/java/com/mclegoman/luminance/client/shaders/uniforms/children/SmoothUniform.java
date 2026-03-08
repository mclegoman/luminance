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
import net.minecraft.util.Identifier;

public class SmoothUniform extends ChildUniform {
    protected UniformValue smooth;
    protected final boolean loop;

    public SmoothUniform(boolean loop) {
        super("smooth");
        this.loop = loop;
    }

    @Override
    public void onRegister(Identifier identifier) {
        super.onRegister(identifier);
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
        if (loop) {
            smooth.loopLerp(uniformValue, shaderTime.getExpDeltaTime(ShaderTime.defaultSpeed), getMin(config, shaderTime).orElse(null), getMax(config, shaderTime).orElse(null));
        } else {
            smooth.lerp(uniformValue, shaderTime.getExpDeltaTime(ShaderTime.defaultSpeed));
        }
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
