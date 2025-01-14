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
    public void preParentUpdate(ShaderTime shaderTime) {

    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        assert parent != null;
        element.set(0, parent.get(UniformConfig.EMPTY, shaderTime).values.get(index));
    }

    @Override
    public void onRegister(String fullName) {

    }

    @Override
    public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
        return element;
    }
}
