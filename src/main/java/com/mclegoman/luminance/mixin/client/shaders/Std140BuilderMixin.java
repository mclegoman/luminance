package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.buffers.Std140Builder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.nio.ByteBuffer;

// fix https://bugs.mojang.com/browse/MC/issues/MC-307206
@Mixin(Std140Builder.class)
public class Std140BuilderMixin {
    @WrapWithCondition(method = "putVec3(FFF)Lcom/mojang/blaze3d/buffers/Std140Builder;", at = @At(value = "INVOKE", target = "Ljava/nio/ByteBuffer;position(I)Ljava/nio/ByteBuffer;"))
    private boolean FixVec3(ByteBuffer instance, int newPosition) {
        return false;
    }

    @ModifyConstant(method = "putVec3(Lorg/joml/Vector3fc;)Lcom/mojang/blaze3d/buffers/Std140Builder;", constant = @Constant(intValue = 16, ordinal = 1))
    private int FixVec3Joml(int constant) {
        return 12;
    }

    @WrapWithCondition(method = "putIVec3(III)Lcom/mojang/blaze3d/buffers/Std140Builder;", at = @At(value = "INVOKE", target = "Ljava/nio/ByteBuffer;position(I)Ljava/nio/ByteBuffer;"))
    private boolean FixIVec3(ByteBuffer instance, int newPosition) {
        return false;
    }

    @ModifyConstant(method = "putIVec3(Lorg/joml/Vector3ic;)Lcom/mojang/blaze3d/buffers/Std140Builder;", constant = @Constant(intValue = 16, ordinal = 1))
    private int FixIVec3Joml(int constant) {
        return 12;
    }
}
