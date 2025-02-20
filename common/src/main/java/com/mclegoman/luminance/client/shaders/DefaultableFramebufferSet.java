package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DefaultableFramebufferSet implements PostEffectProcessor.FramebufferSet {
    // when rendering shaders with the allocator, they cant access fabulous buffers
    // this stops the shaders from rendering, which isnt ideal, and it stops them working with iris
    // by swapping out the FramebufferSet.singleton with this class, it instead just returns an empty buffer
    private Handle<Framebuffer> framebuffer;
    private Handle<Framebuffer> defaultFramebuffer;

    public DefaultableFramebufferSet(FrameGraphBuilder builder, Framebuffer framebuffer) {
        this.framebuffer = builder.createObjectNode("main", framebuffer);
        PersistentFramebufferFactory persistentFramebufferFactory = new PersistentFramebufferFactory(new SimpleFramebufferFactory(framebuffer.textureWidth, framebuffer.textureHeight, framebuffer.useDepthAttachment), null, Identifier.of(Data.getVersion().getID(), "default"));
        defaultFramebuffer = builder.createResourceHandle("luminance:default", persistentFramebufferFactory);
    }

    public void set(Identifier idx, Handle<Framebuffer> framebufferx) {
        if (idx.equals(PostEffectProcessor.MAIN)) {
            framebuffer = framebufferx;
        } else {
            defaultFramebuffer = framebufferx;
        }
    }

    @Nullable
    public Handle<Framebuffer> get(Identifier idx) {
        return idx.equals(PostEffectProcessor.MAIN) ? framebuffer : null;
    }

    @Override
    public Handle<Framebuffer> getOrThrow(Identifier id) {
        Handle<Framebuffer> handle = get(id);
        if (handle == null) {
            return defaultFramebuffer;
        } else {
            return handle;
        }
    }
}
