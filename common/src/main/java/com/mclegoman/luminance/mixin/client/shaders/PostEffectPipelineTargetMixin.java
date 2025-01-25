/*
    Luminance
    Contributor(s): Nettakrim (also indirectly: cputnam-a11y in the fabric discord for showing me how to do the codec wrapping magic)
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineTargetInterface;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.PostEffectPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Function;

@Mixin(PostEffectPipeline.Targets.class)
public interface PostEffectPipelineTargetMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;either(Lcom/mojang/serialization/Codec;Lcom/mojang/serialization/Codec;)Lcom/mojang/serialization/Codec;", remap = false))
    private static <F, S> Codec<Either<F, S>> wrapCreatePersistent(Codec<F> first, Codec<S> second, Operation<Codec<Either<F, S>>> original) {
        return original.call(luminance$codecBuilderPersistent(first), luminance$codecBuilderPersistent(second));
    }

    @Unique
    private static <F> Codec<F> luminance$codecBuilderPersistent(Codec<F> original) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        MapCodec.assumeMapUnsafe(original).forGetter(Function.identity()),
                        Codec.BOOL.lenientOptionalFieldOf("persistent").forGetter(((target) -> Optional.of(((PipelineTargetInterface)target).luminance$getPersistent())))
                )
                .apply(instance, (target, persistent) -> {
                    ((PipelineTargetInterface)target).luminance$setPersistent(persistent.orElse(false));
                    return target;
                })
        );
    }
}
