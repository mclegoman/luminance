/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.shaders.PersistentFramebufferFactory;
import com.mclegoman.luminance.client.shaders.interfaces.PipelineTargetInterface;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectPassInterface;
import net.minecraft.client.gl.*;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("DataFlowIssue")
@Mixin(PostEffectProcessor.class)
public class PostEffectProcessorMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FrameGraphBuilder;createResourceHandle(Ljava/lang/String;Lnet/minecraft/client/util/ClosableFactory;)Lnet/minecraft/client/util/Handle;"), method = "render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", index = 1)
    private ClosableFactory<Framebuffer> replaceFramebufferFactory(ClosableFactory<Framebuffer> factory, @Local Map.Entry<Identifier, PostEffectPipeline.Targets> target) {
        PostEffectPipeline.Targets targets = target.getValue();
        if (!((PipelineTargetInterface)(Object)targets).luminance$getPersistent()) {
            return factory;
        }

        SimpleFramebufferFactory simpleFramebufferFactory = (SimpleFramebufferFactory)factory;
        return new PersistentFramebufferFactory(simpleFramebufferFactory, (PostEffectProcessor)(Object)this, target.getKey());
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void setForceVisit(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets, CallbackInfo ci) {
        for (PostEffectPass postEffectPass : passes) {
            PostEffectPassInterface passInterface = (PostEffectPassInterface)postEffectPass;
            PostEffectPipeline.Targets targets = internalTargets.get(passInterface.luminance$getOutputTarget());

            if (targets == null) continue;
            if (!((PipelineTargetInterface)(Object)targets).luminance$getPersistent()) continue;

            passInterface.luminance$setForceVisit(true);
        }
    }
}
