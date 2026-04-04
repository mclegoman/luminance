/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.DynamicRenderTickCounterInterfact;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterInterfactMixin implements DynamicRenderTickCounterInterfact {
    @Shadow private float tickProgress;

    @Override
    public float luminance$getRawTickProgress() {
        return this.tickProgress;
    }
}
