/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.util.Identifier;

public record PersistentFramebufferFactory(SimpleFramebufferFactory simpleFramebufferFactory, Object source, Identifier target) implements ClosableFactory<Framebuffer> {
    // equal objects in the game renderer pool get reused
    // this means screen-sized buffers will get messed with before they return to where they should be...
    // by changing the factory in PostEffectProcessorMixin with a new record that varies per persistent framebuffer
    // the game will always get the same framebuffer for the target, and nothing else will touch it
    public Framebuffer create() {
        Framebuffer framebuffer = simpleFramebufferFactory.create();
        framebuffer.setClearColor(0,0,0,0);
        framebuffer.clear();
        return framebuffer;
    }

    public void close(Framebuffer framebuffer) {
        framebuffer.delete();
    }
}

