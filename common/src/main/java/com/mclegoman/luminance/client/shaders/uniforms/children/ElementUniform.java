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

import java.util.Optional;

public class ElementUniform extends ChildUniform {
    protected UniformValue element;
    int index;

    public ElementUniform(String name, int index) {
        super(name);
        element = new UniformValue(1);
        this.index = index;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {

    }

    @Override
    public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
        assert parent != null;
        element.set(0, parent.getCache(config, shaderTime).values.get(index));
    }

    @Override
    public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
        return element;
    }

    @Override
    public UniformConfig getDefaultConfig() {
        return EmptyConfig.INSTANCE;
    }

    @Override
    public Optional<UniformValue> getMin() {
        assert parent != null;
        return parent.getMin().map(min -> UniformValue.fromFloat(min.values.get(index), 1));
    }

    @Override
    public Optional<UniformValue> getMax() {
        assert parent != null;
        return parent.getMax().map(max -> UniformValue.fromFloat(max.values.get(index), 1));
    }
}
