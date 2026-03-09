/*
    Luminance
    Contributor(s): Nettakrim (also indirectly: cputnam-a11y in the fabric discord for showing me how to do the codec wrapping magic)
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineTargetInterface;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.PostEffectPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Function;

@Mixin(PostEffectPipeline.Targets.class)
public class PostEffectPipelineTargetMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static <O> Codec<O> wrapCreateOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder, Operation<Codec<O>> original) {
        return original.call(luminance$codecBuilderOverride(builder));
    }

    @Unique
    private static <O> Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> luminance$codecBuilderOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                PipelineTargetInterface.DynamicSize.CODEC.lenientOptionalFieldOf("dynamic_size").forGetter(target -> Optional.ofNullable(((PipelineTargetInterface)target).luminance$getDynamicSize()))
        ).apply(instance, (target, dynamicSize) -> {
            ((PipelineTargetInterface)target).luminance$setDynamicSize(dynamicSize.orElse(null));
            return target;
        });
    }
}
