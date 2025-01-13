package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import net.minecraft.util.math.MathHelper;

public class SmoothUniform extends TreeUniform {
    protected float smooth;

    public SmoothUniform() {
        super("smooth");
    }

    @Override
    public void updateValue(float tickDelta, float deltaTime) {
        if (parent == null) {
            return;
        }

        smooth = MathHelper.lerp(deltaTime, smooth, parent.get());
    }

    @Override
    public float get() {
        return smooth;
    }
}
