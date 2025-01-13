package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;

public class ShaderTime {
    private float tickDelta;
    private float deltaTime;

    private float prevTickDelta = 0.0F;

    public void update(float tickDelta) {
        this.tickDelta = tickDelta;
        deltaTime = ((tickDelta < prevTickDelta ? 1 : 0) + tickDelta-prevTickDelta);
        prevTickDelta = tickDelta;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public float getDeltaTime() {
        return deltaTime;
    }
}
