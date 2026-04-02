package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;

public class RenderTypes {
    public static RenderType WORLD = register(Data.idOf("world"), Shaders::renderUsingAllocator);
    public static RenderType UI = register(Data.idOf("ui"), Shaders::renderUsingAllocator);
    public static RenderType UI_BACKGROUND = register(Data.idOf("ui_background"), Shaders::renderUsingAllocator);
    public static RenderType PANORAMA = register(Data.idOf("panorama"), Shaders::renderUsingAllocator);

    public static RenderType register(Identifier identifier, Renderer renderer) {
        RenderType renderType = new RenderType(identifier, renderer);
        Events.RenderType.register(identifier, renderType);
        return renderType;
    }

    public interface Renderer {
        void render(Identifier id, Shader.Data shader, Framebuffer framebuffer, ObjectAllocator objectAllocator);
    }

    public static class RenderType {
        private final Identifier identifier;
        private final Renderer renderer;

        private RenderType(Identifier identifier, Renderer renderer) {
            this.identifier = identifier;
            this.renderer = renderer;
        }

        public Identifier getIdentifier() {
            return this.identifier;
        }

        public Renderer getRenderer() {
            return this.renderer;
        }

        public void render(Identifier id, Shader.Data shader, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
            this.getRenderer().render(id, shader, framebuffer, objectAllocator);
        }
    }
}
