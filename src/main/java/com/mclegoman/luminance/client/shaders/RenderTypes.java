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
    public static RenderType WORLD = register(Data.idOf("world"), Shaders::renderUsingAllocator, true, false, false, false);
    public static RenderType UI = register(Data.idOf("ui"), Shaders::renderUsingAllocator, false, true, false, true);
    public static RenderType UI_BACKGROUND = register(Data.idOf("ui_background"), Shaders::renderUsingAllocator, false, false, true, true);
    public static RenderType PANORAMA = register(Data.idOf("panorama"), Shaders::renderUsingAllocator, false, false, true, false);

    public static RenderType getFallback() {
        return WORLD;
    }

    public static void render(RenderType type, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        Events.ShaderRender.registry.forEach((id, shaders) -> {
            try {
                renderShaders(type, shaders, id, renderTarget, resourceAllocator);
            } catch (Exception error) {
                Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
            }
        });
    }

    public static void renderShaders(RenderType type, Events.ShaderRenderData shaderRenderData, Identifier id, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        if (shaderRenderData != null) {
            List<Shader.Data> shaders = shaderRenderData.shaders();
            if (shaders != null) shaders.forEach(shader -> {
                try {
                    renderShader(type, id, shader, renderTarget, resourceAllocator, shaderRenderData.disablePhotosensitive().call(shader.shader().getShaderData()));
                } catch (Exception error) {
                    Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
                }
            });
        }
    }

    public static void renderShader(RenderType type, Identifier id, Shader.Data shader, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator) {
        renderShader(type, id, shader, renderTarget, resourceAllocator, false);
    }

    public static void renderShader(RenderType type, Identifier id, Shader.Data shader, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator, boolean disablePhotosensitivity) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        try {
            if (shader == null) return;
            boolean isFallback = type.equals(getFallback());

            Shader shaderInstance = shader.shader();
            if (shaderInstance == null) return;

            ShaderRegistryEntry shaderData = shaderInstance.getShaderData();
            if (shaderData == null) return;

            if (shaderData.isPhotosensitive() && disablePhotosensitivity) return;

            Callable<RenderTypes.RenderType> callableRenderType = shaderInstance.getRenderType();
            if (callableRenderType == null) return;

            RenderType renderType = callableRenderType.call();
            if (renderType == null) return;

            boolean isCorrectType = renderType.equals(type);
            if (!isCorrectType && !isFallback) return;

            boolean shouldFallback = (shaderInstance.getUseDepth() && !renderType.isDepthSupported()) || (shaderData.useFallbackWhenOverUi() && renderType.isOverUi()) || (shaderData.useFallbackWhenUnderUi() && renderType.isUnderUi());
            if (shouldFallback && !renderType.canFallback()) return;

            boolean canRender = (!shouldFallback && isCorrectType) || (shouldFallback && isFallback);
            if (!canRender) return;

            type.render(id, shader, renderTarget, resourceAllocator);
        } catch (Exception error) {
            Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
        }
    }

    public static RenderType register(Identifier identifier, Renderer renderer, boolean isDepthSupported, boolean isOverUi, boolean isUnderUi, boolean canFallback) {
        RenderType renderType = new RenderType(identifier, renderer, isDepthSupported, isOverUi, isUnderUi, canFallback);
        Events.RenderType.register(identifier, renderType);
        return renderType;
    }

    public interface Renderer {
        void render(Identifier id, Shader.Data shader, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator);
    }

    public record RenderType(Identifier identifier, Renderer renderer, boolean isDepthSupported, boolean isOverUi, boolean isUnderUi, boolean canFallback) {
        public void render(Identifier id, Shader.Data shader, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator) {
            this.renderer().render(id, shader, renderTarget, resourceAllocator);
        }
    }
}
