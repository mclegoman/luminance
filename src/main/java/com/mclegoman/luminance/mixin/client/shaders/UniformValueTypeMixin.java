package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.IVec2Uniform;
import com.mclegoman.luminance.client.shaders.IVec4Uniform;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.UniformValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UniformValue.Type.class)
enum UniformValueTypeMixin {
    LUMINANCE_IVEC2("ivec2", IVec2Uniform.CODEC),
    LUMINANCE_IVEC4("ivec4", IVec4Uniform.CODEC);

    @Shadow
    UniformValueTypeMixin(final String string2, final Codec codec) {

    }
}
