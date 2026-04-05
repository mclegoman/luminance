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

public class DeltaUniform extends ChildUniform {
    protected UniformVector delta;
    protected final boolean loop;

    public DeltaUniform(boolean loop) {
        super("delta");
        this.loop = loop;
    }

    @Override
    public void onRegister(Identifier identifier) {
        super.onRegister(identifier);
        assert parent != null;
        delta = new UniformVector(parent.getLength());
    }

    @Override
    public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        delta = parent.getCache(config, shaderTime).copyTo(delta);
    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        UniformVector uniformVector = parent.getCache(config, shaderTime);
        if (loop) {
            delta.loopDelta(uniformVector, parent.getMin(config, shaderTime).orElse(null), parent.getMax(config, shaderTime).orElse(null));
        } else {
            delta.delta(uniformVector);
        }
    }

    @Override
    public UniformVector getCache(UniformConfig config, ShaderTime shaderTime) {
        return delta;
    }

    @Override
    public UniformConfig getDefaultConfig() {
        return EmptyConfig.INSTANCE;
    }
}
