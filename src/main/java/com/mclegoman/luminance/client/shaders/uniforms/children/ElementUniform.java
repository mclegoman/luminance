package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

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
}
