/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformVector;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ChildUniform extends TreeUniform {
    protected ChildUniform(String name) {
        super(name, false);
    }

    @Override
    public int getLength() {
        assert parent != null;
        return parent.getLength();
    }

    @Override
    public Optional<UniformVector> getMin(@Nullable UniformConfig config, @Nullable ShaderTime shaderTime) {
        assert parent != null;
        return parent.getMin(config, shaderTime);
    }

    @Override
    public Optional<UniformVector> getMax(@Nullable UniformConfig config, @Nullable ShaderTime shaderTime) {
        assert parent != null;
        return parent.getMax(config, shaderTime);
    }

    @Override
    public boolean rangeCanChange() {
        assert parent != null;
        return parent.rangeCanChange();
    }
}
