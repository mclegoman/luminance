package com.mclegoman.luminance.mixin.client.shaders;

import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.ShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShaderManager.class)
public interface ShaderManagerAcessor {
    @Accessor("postChainProjectionMatrixBuffer")
    CachedOrthoProjectionMatrixBuffer getPostChainProjectionMatrixBuffer();
}
