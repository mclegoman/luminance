package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

import java.util.Locale;

public class ElementUniform extends ChildUniform {
    protected UniformValue element;
    int index;

    public ElementUniform(String name) {
        super(name);
        element = new UniformValue(1);
        index = name.toLowerCase(Locale.ROOT).charAt(0)-'x';
        if (index == -1) index = 3;
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
        UniformValue uniformValue = parent.get(UniformConfig.EMPTY, shaderTime);
        if (index >= 0 && index < element.values.size()) {
            element.set(0, uniformValue.values.get(index));
        }
    }

    @Override
    public void onRegister(String fullName) {

    }

    @Override
    public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
        return element;
    }
}
