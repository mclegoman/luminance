/*
    Luminance
    Contributor(s): Nettakrim (also indirectly: cputnam-a11y in the fabric discord for showing me how to do the codec wrapping magic)
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.PipelineUniformInterface;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.PostEffectPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin(PostEffectPipeline.Uniform.class)
public class PostEffectPipelineUniformMixin implements PipelineUniformInterface {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static <O> Codec<O> wrapCreateOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder, Operation<Codec<O>> original) {
        return original.call(codecBuilderOverride(builder));
    }

    @Unique
    private static <O> Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> codecBuilderOverride(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                Codec.STRING.sizeLimitedListOf(4).lenientOptionalFieldOf("override").forGetter((uniform -> ((PipelineUniformInterface)uniform).luminance$getOverride())),
                ConfigData.CODEC.listOf().lenientOptionalFieldOf("config").forGetter((uniform -> ((PipelineUniformInterface)uniform).luminance$getConfig()))
        ).apply(instance, (uniform, override, config) -> {
            override.ifPresent(strings -> ((PipelineUniformInterface) uniform).luminance$setOverride(strings));
            config.ifPresent(list -> ((PipelineUniformInterface) uniform).luminance$setConfig(list));
            return uniform;
        });
    }


    @Unique
    private List<String> override;

    @Unique
    public Optional<List<String>> luminance$getOverride() {
        return Optional.ofNullable(override);
    }

    @Override
    public void luminance$setOverride(List<String> override) {
        this.override = override;
    }


    @Unique
    private List<ConfigData> config;

    @Override
    public Optional<List<ConfigData>> luminance$getConfig() {
        return Optional.ofNullable(config);
    }

    @Override
    public void luminance$setConfig(List<ConfigData> config) {
        this.config = config;
    }
}
