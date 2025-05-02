/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import net.minecraft.util.math.MathHelper;

public class ShaderTime {
    private float tickDelta;
    private float deltaTime;

    private float prevTickDelta = 0.0F;

    private float elapsedTime;

    public static final float defaultSpeed = 1f;
    private float expDelta;

    // current position in the rendering pipeline
    public static Shader.RenderType currentRendertype;

    public void update(float tickDelta) {
        this.tickDelta = tickDelta;
        deltaTime = ((tickDelta < prevTickDelta ? 1 : 0) + tickDelta-prevTickDelta);
        prevTickDelta = tickDelta;

        elapsedTime += deltaTime/24000f;
        if (elapsedTime > 1) elapsedTime -= 1;

        expDelta = (float)(1-Math.exp(-defaultSpeed*deltaTime));
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    // factor for proper lerp smoothing, as per https://www.youtube.com/watch?v=LSNQuFEDOyQ
    // speed of 1 is "slow", speed of 25 is "fast"
    public float getExpDeltaTime(float speed) {
        if (speed == defaultSpeed) {
            return expDelta;
        }
        return (float)(1-Math.exp(-speed*deltaTime));
    }

    public float getModuloTime(float modulo) {
        return MathHelper.floorMod(elapsedTime*1200, modulo);
    }
}
