package com.mclegoman.luminance.mixin.client.shaders;

import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(PostEffectProcessor.class)
public interface PostEffectProcessorInvoker {
    @Invoker(value = "<init>")
    static PostEffectProcessor init(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets) {
        throw new AssertionError("Implemented by Mixin");
    }
}
