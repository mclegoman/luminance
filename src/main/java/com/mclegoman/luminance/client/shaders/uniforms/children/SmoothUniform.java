package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import net.minecraft.util.math.MathHelper;

public class SmoothUniform extends TreeUniform {
    protected float smooth;

    public SmoothUniform() {
        super("smooth");
    }

    @Override
    public void updateValue(ShaderTime shaderTime) {
        if (parent == null) {
            return;
        }

        smooth = MathHelper.lerp(shaderTime.getDeltaTime(), smooth, parent.get(UniformConfig.EMPTY, shaderTime));
    }

    @Override
    public float get(UniformConfig config, ShaderTime shaderTime) {
        return smooth;
    }
}
