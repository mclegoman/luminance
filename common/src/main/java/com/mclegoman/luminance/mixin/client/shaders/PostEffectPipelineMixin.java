package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineInterface;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(PostEffectPipeline.class)
public class PostEffectPipelineMixin implements PipelineInterface {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"), remap = false)
    private static <O> Codec<O> wrapCreateOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder, Operation<Codec<O>> original) {
        return original.call(luminance$codecBuilderOverride(builder));
    }

    @Unique
    private static <O> Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> luminance$codecBuilderOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                Codec.unboundedMap(Identifier.CODEC, PostEffectPipeline.Pass.CODEC.listOf()).lenientOptionalFieldOf("custom_passes").forGetter((pipeline -> ((PipelineInterface)pipeline).luminance$getCustomPasses()))
        ).apply(instance, (pipeline, passes) -> {
            passes.ifPresent(identifierListMap -> ((PipelineInterface)pipeline).luminance$setCustomPasses(identifierListMap));
            return pipeline;
        });
    }

    @Unique
    private Map<Identifier, List<PostEffectPipeline.Pass>> luminance$customPasses;

    @Override
    public Optional<Map<Identifier, List<PostEffectPipeline.Pass>>> luminance$getCustomPasses() {
        return Optional.ofNullable(luminance$customPasses);
    }

    @Override
    public void luminance$setCustomPasses(Map<Identifier, List<PostEffectPipeline.Pass>> passes) {
        luminance$customPasses = passes;
    }
}
