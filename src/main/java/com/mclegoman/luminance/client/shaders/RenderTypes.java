package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;

import java.util.List;

public class RenderTypes {
    public static RenderType WORLD = register(Data.idOf("world"), Shaders::renderUsingAllocator, true, false, false);
    public static RenderType UI = register(Data.idOf("ui"), Shaders::renderUsingAllocator, false, true, false);
    public static RenderType UI_BACKGROUND = register(Data.idOf("ui_background"), Shaders::renderUsingAllocator, false, false, true);
    public static RenderType PANORAMA = register(Data.idOf("panorama"), Shaders::renderUsingAllocator, false, false, true);

    public static RenderType getFallback() {
        return WORLD;
    }

    public static void render(RenderType type, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
        Events.ShaderRender.registry.forEach((id, shaders) -> renderShaders(type, shaders, id, framebuffer, objectAllocator));
    }

    public static void renderShaders(RenderType type, List<Shader.Data> shaders, Identifier id, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
        if (!ClientData.minecraft.gameRenderer.isRenderingPanorama()) {
            if (shaders != null) shaders.forEach(shader -> {
                try {
                    if (renderShader(type, id, shader, framebuffer, objectAllocator).equals(RenderReturn.USE_FALLBACK)) getFallback().render(id, shader, framebuffer, objectAllocator);
                } catch (Exception error) {
                    Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render {} shader with id: {}:{}", type.getIdentifier(), id, error));
                }
            });
        }
    }

    public static RenderReturn renderShader(RenderType type, Identifier id, Shader.Data shader, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
        try {
            if (shader == null || shader.shader() == null || shader.shader().getShaderData() == null) return RenderReturn.MISSING_DATA;
            if (!shader.shader().getRenderType().call().equals(type.getIdentifier())) return RenderReturn.INVALID_TYPE;
            if (shader.shader().getUseDepth() && !type.isDepthSupported()) return RenderReturn.USE_FALLBACK;
            if (shader.shader().getShaderData().getDisableUiRenderType() && type.isOverUi()) return RenderReturn.USE_FALLBACK;
            if (shader.shader().getShaderData().getDisableUiBackgroundRenderTypes() && type.isUnderUi()) return RenderReturn.USE_FALLBACK;
            type.render(id, shader, framebuffer, objectAllocator);
            return RenderReturn.COMPLETED;
        } catch (Exception error) {
            Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to render {} shader with id: {}:{}", type.getIdentifier(), id, error));
        }
        return RenderReturn.USE_FALLBACK;
    }

    public static RenderType register(Identifier identifier, Renderer renderer, boolean isDepthSupported, boolean isOverUi, boolean isUnderUi) {
        RenderType renderType = new RenderType(identifier, renderer, isDepthSupported, isOverUi, isUnderUi);
        Events.RenderType.register(identifier, renderType);
        return renderType;
    }

    public interface Renderer {
        void render(Identifier id, Shader.Data shader, Framebuffer framebuffer, ObjectAllocator objectAllocator);
    }

    public static class RenderType {
        private final Identifier identifier;
        private final Renderer renderer;
        private final boolean isDepthSupported;
        private final boolean isOverUi;
        private final boolean isUnderUi;

        public RenderType(Identifier identifier, Renderer renderer, boolean isDepthSupported, boolean isOverUi, boolean isUnderUi) {
            this.identifier = identifier;
            this.renderer = renderer;
            this.isDepthSupported = isDepthSupported;
            this.isOverUi = isOverUi;
            this.isUnderUi = isUnderUi;
        }

        public Identifier getIdentifier() {
            return this.identifier;
        }

        public Renderer getRenderer() {
            return this.renderer;
        }

        public boolean isDepthSupported() {
            return this.isDepthSupported;
        }

        public boolean isOverUi() {
            return this.isOverUi;
        }

        public boolean isUnderUi() {
            return this.isUnderUi;
        }

        public void render(Identifier id, Shader.Data shader, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
            this.getRenderer().render(id, shader, framebuffer, objectAllocator);
        }
    }

    public enum RenderReturn {
        MISSING_DATA,
        INVALID_TYPE,
        USE_FALLBACK,
        COMPLETED
    }
}
