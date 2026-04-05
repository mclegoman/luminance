package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.Callable;

public class RenderTypes {
    public static RenderType WORLD = register(Data.idOf("world"), Shaders::renderUsingAllocator, true, false, false);
    public static RenderType UI = register(Data.idOf("ui"), Shaders::renderUsingAllocator, false, true, false);
    public static RenderType UI_BACKGROUND = register(Data.idOf("ui_background"), Shaders::renderUsingAllocator, false, false, true);
    public static RenderType PANORAMA = register(Data.idOf("panorama"), Shaders::renderUsingAllocator, false, false, true);

    public static RenderType getFallback() {
        return WORLD;
    }

    public static void render(RenderType type, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        Events.ShaderRender.registry.forEach((id, shaders) -> {
            try {
                renderShaders(type, shaders, id, framebuffer, objectAllocator);
            } catch (Exception error) {
                Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
            }
        });
    }

    public static void renderShaders(RenderType type, Events.ShaderRenderData shaderRenderData, Identifier id, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        if (shaderRenderData != null) {
            List<Shader.Data> shaders = shaderRenderData.shaders();
            if (shaders != null) shaders.forEach(shader -> {
                try {
                    renderShader(type, id, shader, framebuffer, objectAllocator, shaderRenderData.disablePhotosensitive().call(shader.shader().getShaderData()));
                } catch (Exception error) {
                    Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
                }
            });
        }
    }

    public static void renderShader(RenderType type, Identifier id, Shader.Data shader, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator) {
        renderShader(type, id, shader, framebuffer, objectAllocator, false);
    }

    public static void renderShader(RenderType type, Identifier id, Shader.Data shader, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator, boolean disablePhotosensitivity) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        try {
            if (shader == null) return;
            boolean isFallback = type.equals(getFallback());

            Shader shaderInstance = shader.shader();
            if (shaderInstance == null) return;

            ShaderRegistryEntry shaderData = shaderInstance.getShaderData();
            if (shaderData == null) return;

            if (shaderData.isPhotosensitive() && disablePhotosensitivity) return;

            Callable<Identifier> callableRenderType = shaderInstance.getRenderType();
            if (callableRenderType == null) return;

            Identifier renderType = callableRenderType.call();
            if (renderType == null) return;

            boolean isCorrectType = renderType.equals(type.identifier());
            if (!isCorrectType && !isFallback) return;

            boolean useFallback = (shaderInstance.getUseDepth() && !type.isDepthSupported()) || (shaderData.useFallbackWhenOverUi() && type.isOverUi()) || (shaderData.useFallbackWhenUnderUi() && type.isUnderUi());
            if (!useFallback && !isCorrectType) return;

            if (!useFallback || isFallback) type.render(id, shader, framebuffer, objectAllocator);
        } catch (Exception error) {
            Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
        }
    }

    public static RenderType register(Identifier identifier, Renderer renderer, boolean isDepthSupported, boolean isOverUi, boolean isUnderUi) {
        RenderType renderType = new RenderType(identifier, renderer, isDepthSupported, isOverUi, isUnderUi);
        Events.RenderType.register(identifier, renderType);
        return renderType;
    }

    public interface Renderer {
        void render(Identifier id, Shader.Data shader, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator);
    }

    public record RenderType(Identifier identifier, Renderer renderer, boolean isDepthSupported, boolean isOverUi, boolean isUnderUi) {
        public void render(Identifier id, Shader.Data shader, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator) {
            this.renderer().render(id, shader, framebuffer, objectAllocator);
        }
    }
}
