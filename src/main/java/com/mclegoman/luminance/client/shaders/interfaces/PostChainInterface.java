/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.PostChain;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface PostChainInterface {
    @Nullable @Contract("null -> !null")
    List<PostPass> luminance$getPasses(@Nullable Identifier chain);

    void luminance$render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle, @Nullable Identifier chain);

    Set<Identifier> luminance$getCustomChainNames();

    boolean luminance$usesDepth();

    boolean luminance$usesImprovedTransparency();

    boolean luminance$usesPersistentBuffers();

    void luminance$setPersistentBufferSource(@Nullable Identifier source);
}
