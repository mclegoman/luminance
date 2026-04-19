/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.internal.InternalPostChainConfigTargetInterface;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.PostChainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Function;

@Mixin(PostChainConfig.InternalTarget.class)
public class PostChainConfigTargetsMixin implements InternalPostChainConfigTargetInterface {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static <O> Codec<O> wrapCreateOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder, Operation<Codec<O>> original) {
        return original.call(luminance$codecBuilderOverride(builder));
    }

    @Unique
    private static <O> Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> luminance$codecBuilderOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                InternalPostChainConfigTargetInterface.DynamicSize.CODEC.lenientOptionalFieldOf("dynamic_size").forGetter(target -> Optional.ofNullable(((InternalPostChainConfigTargetInterface)target).luminance$getDynamicSize()))
        ).apply(instance, (target, dynamicSize) -> {
            ((InternalPostChainConfigTargetInterface)target).luminance$setDynamicSize(dynamicSize.orElse(null));
            return target;
        });
    }

    @Unique
    private DynamicSize luminance$dynamicSize;

    @Override
    public DynamicSize luminance$getDynamicSize() {
        return luminance$dynamicSize;
    }

    @Override
    public void luminance$setDynamicSize(DynamicSize dynamicSize) {
        this.luminance$dynamicSize = dynamicSize;
    }
}
