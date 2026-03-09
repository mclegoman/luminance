/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

// private class cant be targeted directly
@Mixin(targets = "net.minecraft.client.render.FrameGraphBuilder$FramePassImpl")
public class FramePassMixin implements FramePassInterface {
    @Shadow @Final int id;
    @Unique private boolean luminance$forceVisit;

    @Override
    public void luminance$setForceVisit(boolean to) {
        luminance$forceVisit = to;
    }

    @Override
    public boolean luminance$getForceVisit() {
        return luminance$forceVisit;
    }

    @Override
    public int luminance$getId() {
        return id;
    }
}
