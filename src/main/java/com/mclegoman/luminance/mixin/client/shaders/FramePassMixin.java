package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net.minecraft.client.render.FrameGraphBuilder$FramePass")
public class FramePassMixin implements FramePassInterface {
    @Shadow @Final int id;
    @Unique private boolean forceVisit;

    @Override
    public void luminance$setForceVisit(boolean to) {
        forceVisit = to;
    }

    @Override
    public boolean luminance$getForceVisit() {
        return forceVisit;
    }

    @Override
    public int luminance$getId() {
        return id;
    }
}
