/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import net.minecraft.util.Mth;

public class ShaderTime {
    private float tickProgress;
    private float deltaTime;

    private float prevTickProgress = 0.0F;

    private float elapsedTime;

    public static final float defaultSpeed = 1f;
    private float expDelta;

    // current position in the rendering pipeline
    public static RenderLocations.RenderLocation<?> currentRenderLocation;

    public void update(float tickProgress) {
        this.tickProgress = tickProgress;
        deltaTime = ((tickProgress < prevTickProgress ? 1 : 0) + tickProgress-prevTickProgress);
        prevTickProgress = tickProgress;

        elapsedTime += deltaTime/24000f;
        if (elapsedTime > 1) elapsedTime -= 1;

        expDelta = (float)(1-Math.exp(-defaultSpeed*deltaTime));
    }

    public float getTickProgress() {
        return tickProgress;
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
        return Mth.positiveModulo(elapsedTime*1200, modulo);
    }
}
