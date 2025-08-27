/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.util.ObjectAllocator;
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
    @Shadow @Final private List<RenderPass> passes;

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/BitSet;cardinality()I"), method = "run(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/FrameGraphBuilder$Profiler;)V")
    private void forceVisits(ObjectAllocator allocator, FrameGraphBuilder.Profiler profiler, CallbackInfo ci, @Local(ordinal = 0) BitSet bitSet) {
        for (RenderPass renderPass : passes) {
            if (renderPass instanceof FramePassInterface framePassInterface && framePassInterface.luminance$getForceVisit()) {
                // this is a slightly inelegant way to force framePasses to not get culled
                // but it is easy to work with
                bitSet.set(framePassInterface.luminance$getId());
            }
        }
    }
}
