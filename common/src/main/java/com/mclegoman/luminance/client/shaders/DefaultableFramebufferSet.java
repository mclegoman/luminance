package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultableFramebufferSet implements PostEffectProcessor.FramebufferSet {
    // when rendering shaders with the allocator, they cant access fabulous buffers
    // this stops the shaders from rendering, which isnt ideal, and it stops them working with iris
    // by swapping out the FramebufferSet.singleton with this class, it instead just returns an empty buffer
    private Handle<Framebuffer> framebuffer;
    private Handle<Framebuffer> defaultFramebuffer;

    @Nullable
    private final Set<Identifier> useDefaultFor;

    public static Set<Identifier> fabulous = new HashSet<>(List.of(
            Identifier.ofVanilla("translucent"),
            Identifier.ofVanilla("item_entity"),
            Identifier.ofVanilla("particles"),
            Identifier.ofVanilla("weather"),
            Identifier.ofVanilla("clouds")
    ));

    public DefaultableFramebufferSet(FrameGraphBuilder builder, Framebuffer framebuffer, @Nullable Set<Identifier> useDefaultFor) {
        this.framebuffer = builder.createObjectNode("main", framebuffer);
        PersistentFramebufferFactory persistentFramebufferFactory = new PersistentFramebufferFactory(new SimpleFramebufferFactory(framebuffer.textureWidth, framebuffer.textureHeight, framebuffer.useDepthAttachment), null, Identifier.of(Data.getVersion().getID(), "default"));
        this.defaultFramebuffer = builder.createResourceHandle("luminance:default", persistentFramebufferFactory);
        this.useDefaultFor = useDefaultFor;
    }

    private DefaultableFramebufferSet(Handle<Framebuffer> framebuffer, Handle<Framebuffer> defaultFramebuffer, @Nullable Set<Identifier> useDefaultFor) {
        this.framebuffer = framebuffer;
        this.defaultFramebuffer = defaultFramebuffer;
        this.useDefaultFor = useDefaultFor;
    }

    public static PostEffectProcessor.FramebufferSet addFabulousIfAbsent(DefaultFramebufferSet defaultFramebufferSet, FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory factory) {
        if (defaultFramebufferSet.translucentFramebuffer != null) {
            return defaultFramebufferSet;
        }
        PersistentFramebufferFactory persistentFramebufferFactory = new PersistentFramebufferFactory(factory, null, Identifier.of(Data.getVersion().getID(), "fabulous"));
        return new DefaultableFramebufferSet(defaultFramebufferSet.mainFramebuffer, frameGraphBuilder.createResourceHandle("luminance:default", persistentFramebufferFactory), fabulous);
    }

    public void set(Identifier id, Handle<Framebuffer> framebufferx) {
        if (id.equals(PostEffectProcessor.MAIN)) {
            framebuffer = framebufferx;
        } else if (useDefault(id)) {
            defaultFramebuffer = framebufferx;
        } else {
            throw new IllegalArgumentException("No target with id " + id);
        }
    }

    @Nullable
    public Handle<Framebuffer> get(Identifier id) {
        return id.equals(PostEffectProcessor.MAIN) ? framebuffer : null;
    }

    @Override
    public Handle<Framebuffer> getOrThrow(Identifier id) {
        Handle<Framebuffer> handle = get(id);
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
