package com.mclegoman.luminance.client.shaders;

import net.minecraft.util.math.MathHelper;

public class ShaderTime {
    private float tickDelta;
    private float deltaTime;

    private float prevTickDelta = 0.0F;

    private float elapsedTime;

    public void update(float tickDelta) {
        this.tickDelta = tickDelta;
        deltaTime = ((tickDelta < prevTickDelta ? 1 : 0) + tickDelta-prevTickDelta);
        prevTickDelta = tickDelta;

        elapsedTime += deltaTime/24000f;
        if (elapsedTime > 1) elapsedTime -= 1;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getModuloTime(float modulo) {
        return MathHelper.floorMod(elapsedTime*1200, modulo);
    }
}
