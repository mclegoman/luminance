package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class SmoothUniform extends TreeUniform<Float, Float> {
    protected float smooth;

    public SmoothUniform() {
        super("smooth");
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        assert parent != null;
        smooth = MathHelper.lerp(shaderTime.getDeltaTime(), smooth, parent.get(UniformConfig.EMPTY, shaderTime));
    }

    @Override
    public Float get(UniformConfig config, ShaderTime shaderTime) {
        return smooth;
    }

    @Override
    public Optional<Float> getMin() {
        assert parent != null;
        return parent.getMin();
    }

    @Override
    public Optional<Float> getMax() {
        assert parent != null;
        return parent.getMax();
    }
}
