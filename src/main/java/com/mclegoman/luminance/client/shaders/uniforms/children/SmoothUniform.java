/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformVector;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import net.minecraft.resources.Identifier;

public class SmoothUniform extends ChildUniform {
    protected UniformVector smooth;
    protected final boolean loop;

    public SmoothUniform(boolean loop) {
        super("smooth");
        this.loop = loop;
    }

    @Override
    public void onRegister(Identifier identifier) {
        super.onRegister(identifier);
        assert parent != null;
        smooth = new UniformVector(parent.getLength());
    }

    @Override
    public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {

    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        UniformVector uniformVector = parent.getCache(config, shaderTime);
        if (loop) {
            smooth.loopLerp(uniformVector, shaderTime.getExpDeltaTime(ShaderTime.defaultSpeed), getMin(config, shaderTime).orElse(null), getMax(config, shaderTime).orElse(null));
        } else {
            smooth.lerp(uniformVector, shaderTime.getExpDeltaTime(ShaderTime.defaultSpeed));
        }
    }

    @Override
    public UniformVector getCache(UniformConfig config, ShaderTime shaderTime) {
        return smooth;
    }

    @Override
    public UniformConfig getDefaultConfig() {
        return EmptyConfig.INSTANCE;
    }
}
