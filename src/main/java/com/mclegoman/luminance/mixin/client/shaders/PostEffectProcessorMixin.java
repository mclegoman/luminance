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
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(PostEffectProcessor.class)
public class PostEffectProcessorMixin {
    @SuppressWarnings("DataFlowIssue")
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FrameGraphBuilder;createResourceHandle(Ljava/lang/String;Lnet/minecraft/client/util/ClosableFactory;)Lnet/minecraft/client/util/Handle;"), method = "render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", index = 1)
    private ClosableFactory<Framebuffer> replaceFramebufferFactory(ClosableFactory<Framebuffer> factory, @Local Map.Entry<Identifier, PostEffectPipeline.Targets> target) {
        PostEffectPipeline.Targets targets = target.getValue();
        if (!((PipelineTargetInterface)(Object)targets).luminance$getPersistent()) {
            return factory;
        }

        SimpleFramebufferFactory simpleFramebufferFactory = (SimpleFramebufferFactory)factory;
        return new PersistentFramebufferFactory(simpleFramebufferFactory, (PostEffectProcessor)(Object)this, target.getKey());
    }
}
