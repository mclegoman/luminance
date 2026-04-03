package com.mclegoman.luminance.mixin.client.shaders;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// fix https://bugs.mojang.com/browse/MC/issues/MC-307206
@Mixin(Std140SizeCalculator.class)
public class Std140SizeCalculatorMixin {
    @ModifyConstant(method = "putVec3", constant = @Constant(intValue = 16, ordinal = 1))
    private int FixVec3(int constant) {
        return 12;
    }

    @ModifyConstant(method = "putIVec3", constant = @Constant(intValue = 16, ordinal = 1))
    private int FixIVec3(int constant) {
        return 12;
    }
}
