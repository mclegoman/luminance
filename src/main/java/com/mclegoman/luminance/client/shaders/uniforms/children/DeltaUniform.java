package com.mclegoman.luminance.client.shaders.uniforms.children;

import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;

public class DeltaUniform extends TreeUniform {
    protected float current;
    protected float prev;

    public DeltaUniform() {
        super("delta");
    }

    @Override
    public void updateValue(float tickDelta, float deltaTime) {
        if (parent == null) {
            return;
        }

        prev = current;
        current = parent.get();
    }

    @Override
    public float get() {
        return current-prev;
    }
}
