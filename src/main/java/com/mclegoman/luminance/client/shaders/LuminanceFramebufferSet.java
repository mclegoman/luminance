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

public class LuminanceFramebufferSet implements PostChain.TargetBundle {
    // when rendering shaders with the allocator, they cant access fabulous buffers
    // this stops the shaders from rendering, which isnt ideal, and it stops them working with iris
    // by swapping out the FramebufferSet.singleton with this class, it instead just returns an empty buffer
    private ResourceHandle<RenderTarget> mainFramebuffer;
    private ResourceHandle<RenderTarget> defaultFramebuffer;

    @Nullable
    private final Set<Identifier> useDefaultFor;

    public static Set<Identifier> fabulous = new HashSet<>(List.of(
            Identifier.withDefaultNamespace("translucent"),
            Identifier.withDefaultNamespace("item_entity"),
            Identifier.withDefaultNamespace("particles"),
            Identifier.withDefaultNamespace("weather"),
            Identifier.withDefaultNamespace("clouds")
    ));

    public LuminanceFramebufferSet(FrameGraphBuilder builder, RenderTarget mainFramebuffer, @Nullable Set<Identifier> useDefaultFor) {
        this.mainFramebuffer = builder.importExternal("main", mainFramebuffer);
        PersistentFramebufferFactory persistentFramebufferFactory = new PersistentFramebufferFactory(new RenderTargetDescriptor(mainFramebuffer.width, mainFramebuffer.height, mainFramebuffer.useDepth, 0), null, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "default"), 0);
        this.defaultFramebuffer = builder.createInternal("luminance:default", persistentFramebufferFactory);
        this.useDefaultFor = useDefaultFor;
    }

    private LuminanceFramebufferSet(ResourceHandle<RenderTarget> mainFramebuffer, ResourceHandle<RenderTarget> defaultFramebuffer, @Nullable Set<Identifier> useDefaultFor) {
        this.mainFramebuffer = mainFramebuffer;
        this.defaultFramebuffer = defaultFramebuffer;
        this.useDefaultFor = useDefaultFor;
    }

    public static PostChain.TargetBundle addFabulousIfAbsent(LevelTargetBundle defaultFramebufferSet, FrameGraphBuilder frameGraphBuilder, RenderTargetDescriptor factory) {
        if (defaultFramebufferSet.translucent != null) {
            return defaultFramebufferSet;
        }
        PersistentFramebufferFactory persistentFramebufferFactory = new PersistentFramebufferFactory(factory, null, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "fabulous"), 0);
        return new LuminanceFramebufferSet(defaultFramebufferSet.main, frameGraphBuilder.createInternal("luminance:default", persistentFramebufferFactory), fabulous);
    }

    public void replace(Identifier id, ResourceHandle<RenderTarget> framebuffer) {
        if (id.equals(PostChain.MAIN_TARGET_ID)) {
            mainFramebuffer = framebuffer;
        } else if (useDefault(id)) {
            defaultFramebuffer = framebuffer;
        } else {
            throw new IllegalArgumentException("No target with id " + id);
        }
    }

    @Nullable
    public ResourceHandle<RenderTarget> get(Identifier id) {
        return id.equals(PostChain.MAIN_TARGET_ID) ? mainFramebuffer : null;
    }

    @Override
    public ResourceHandle<RenderTarget> getOrThrow(Identifier id) {
        ResourceHandle<RenderTarget> handle = get(id);
        if (handle == null) {
            if (useDefault(id)) {
                return defaultFramebuffer;
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
