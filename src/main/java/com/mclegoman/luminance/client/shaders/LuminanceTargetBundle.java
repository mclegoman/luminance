/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.common.data.Data;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import net.minecraft.client.renderer.LevelTargetBundle;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.ResourceHandle;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LuminanceTargetBundle implements PostChain.TargetBundle {
    // when rendering shaders with the allocator, they cant access fabulous buffers
    // this stops the shaders from rendering, which isnt ideal, and it stops them working with iris
    // by swapping out the usual target bundle with this class, it instead just returns an empty buffer
    private ResourceHandle<RenderTarget> mainRenderTarget;
    private ResourceHandle<RenderTarget> defaultRenderTarget;

    @Nullable
    private final Set<Identifier> useDefaultFor;

    public static Set<Identifier> fabulous = new HashSet<>(List.of(
            Identifier.withDefaultNamespace("translucent"),
            Identifier.withDefaultNamespace("item_entity"),
            Identifier.withDefaultNamespace("particles"),
            Identifier.withDefaultNamespace("weather"),
            Identifier.withDefaultNamespace("clouds")
    ));

    public LuminanceTargetBundle(FrameGraphBuilder builder, RenderTarget mainRenderTarget, @Nullable Set<Identifier> useDefaultFor) {
        this.mainRenderTarget = builder.importExternal("main", mainRenderTarget);
        PersistentRenderTargetDescriptor persistentRenderTargetDescriptor = new PersistentRenderTargetDescriptor(new RenderTargetDescriptor(mainRenderTarget.width, mainRenderTarget.height, mainRenderTarget.useDepth, 0), null, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "default"), 0);
        this.defaultRenderTarget = builder.createInternal("luminance:default", persistentRenderTargetDescriptor);
        this.useDefaultFor = useDefaultFor;
    }

    private LuminanceTargetBundle(ResourceHandle<RenderTarget> mainRenderTarget, ResourceHandle<RenderTarget> defaultRenderTarget, @Nullable Set<Identifier> useDefaultFor) {
        this.mainRenderTarget = mainRenderTarget;
        this.defaultRenderTarget = defaultRenderTarget;
        this.useDefaultFor = useDefaultFor;
    }

    public static PostChain.TargetBundle addFabulousIfAbsent(LevelTargetBundle levelTargetBundle, FrameGraphBuilder frameGraphBuilder, RenderTargetDescriptor factory) {
        if (levelTargetBundle.translucent != null) {
            return levelTargetBundle;
        }
        PersistentRenderTargetDescriptor persistentRenderTargetDescriptor = new PersistentRenderTargetDescriptor(factory, null, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "fabulous"), 0);
        return new LuminanceTargetBundle(levelTargetBundle.main, frameGraphBuilder.createInternal("luminance:default", persistentRenderTargetDescriptor), fabulous);
    }

    public void replace(Identifier id, ResourceHandle<RenderTarget> renderTarget) {
        if (id.equals(PostChain.MAIN_TARGET_ID)) {
            mainRenderTarget = renderTarget;
        } else if (useDefault(id)) {
            defaultRenderTarget = renderTarget;
        } else {
            throw new IllegalArgumentException("No target with id " + id);
        }
    }

    @Nullable
    public ResourceHandle<RenderTarget> get(Identifier id) {
        return id.equals(PostChain.MAIN_TARGET_ID) ? mainRenderTarget : null;
    }

    @Override
    public ResourceHandle<RenderTarget> getOrThrow(Identifier id) {
        ResourceHandle<RenderTarget> handle = get(id);
        if (handle == null) {
            if (useDefault(id)) {
                return defaultRenderTarget;
            } else {
                throw new IllegalArgumentException("Missing target with id " + id);
            }
        } else {
            return handle;
        }
    }

    public boolean useDefault(Identifier id) {
        return useDefaultFor == null || useDefaultFor.contains(id);
    }
}
