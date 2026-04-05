/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.List;

@Mixin(FrameGraphBuilder.class)
public class FrameGraphBuilderMixin {
    @Shadow @Final private List<FramePass> passes;

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/BitSet;cardinality()I"), method = "execute(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder$Inspector;)V")
    private void forceVisits(GraphicsResourceAllocator allocator, FrameGraphBuilder.Inspector profiler, CallbackInfo ci, @Local(ordinal = 0) BitSet bitSet) {
        for (FramePass renderPass : passes) {
            if (renderPass instanceof FramePassInterface framePassInterface && framePassInterface.luminance$getForceVisit()) {
                // this is a slightly inelegant way to force framePasses to not get culled
                // but it is easy to work with
                bitSet.set(framePassInterface.luminance$getId());
            }
        }
    }
}
