/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PostChainConfigInterface;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(PostChainConfig.class)
public class PostChainConfigMixin implements PostChainConfigInterface {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"), remap = false)
    private static <O> Codec<O> wrapCreateOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder, Operation<Codec<O>> original) {
        return original.call(luminance$codecBuilderOverride(builder));
    }

    @Unique
    private static <O> Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> luminance$codecBuilderOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                Codec.unboundedMap(Identifier.CODEC, PostChainConfig.Pass.CODEC.listOf()).lenientOptionalFieldOf("custom_chains").forGetter((pipeline -> ((PostChainConfigInterface)pipeline).luminance$getCustomChains()))
        ).apply(instance, (pipeline, passes) -> {
            passes.ifPresent(identifierListMap -> ((PostChainConfigInterface)pipeline).luminance$setCustomChains(identifierListMap));
            return pipeline;
        });
    }

    @Unique
    private Map<Identifier, List<PostChainConfig.Pass>> luminance$customChains;

    @Override
    public Optional<Map<Identifier, List<PostChainConfig.Pass>>> luminance$getCustomChains() {
        return Optional.ofNullable(luminance$customChains);
    }

    @Override
    public void luminance$setCustomChains(Map<Identifier, List<PostChainConfig.Pass>> passes) {
        luminance$customChains = passes;
    }
}
