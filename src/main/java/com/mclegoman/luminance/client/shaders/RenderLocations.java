/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.Runnables;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.Callable;

public class RenderLocations {
    public static RenderLocation<Runnables.LevelRender.Data> LEVEL = register(Data.idOf("level"), Shaders::renderFromLevelData, true, UIType.NONE, true, false);
    public static RenderLocation<Runnables.GameRender.Data> GAME = register(Data.idOf("game"), Shaders::renderFromGameData, true, UIType.NONE, true);
    public static RenderLocation<Runnables.GameRender.Data> UI = register(Data.idOf("ui"), Shaders::renderFromGameData, false, UIType.OVER, true);
    public static RenderLocation<Runnables.GameRender.Data> UI_BACKGROUND = register(Data.idOf("ui_background"), Shaders::renderFromGameData, false, UIType.UNDER, true);
    public static RenderLocation<Runnables.GameRender.Data> PANORAMA = register(Data.idOf("panorama"), Shaders::renderFromGameData, false, UIType.UNDER, false);

    public static RenderLocation<?> getFallback() {
        return getFallback(false);
    }

    public static RenderLocation<?> getFallback(boolean isFabulous) {
        return isFabulous ? LEVEL : GAME;
    }

    public static <T> void render(RenderLocation<T> type, T data) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        Events.ShaderRender.registry.forEach((id, shaders) -> {
            try {
                renderShaders(type, data, shaders, id);
            } catch (Exception error) {
                Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
            }
        });
    }

    public static <T> void renderShaders(RenderLocation<T> type, T data, Events.ShaderRenderData shaderRenderData, Identifier id) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        if (shaderRenderData != null) {
            List<Shader.Data> shaders = shaderRenderData.shaders();
            if (shaders != null) shaders.forEach(shader -> {
                try {
                    renderShader(type, data, id, shader, shaderRenderData.disablePhotosensitive().call(shader.shader().getShaderData()));
                } catch (Exception error) {
                    Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
                }
            });
        }
    }

    public static <T> void renderShader(RenderLocation<T> type, T data, Identifier id, Shader.Data shader) {
        renderShader(type, data, id, shader, false);
    }

    public static <T> void renderShader(RenderLocation<T> type, T data, Identifier id, Shader.Data shader, boolean disablePhotosensitivity) {
        if (ClientData.minecraft.gameRenderer.isPanoramicMode()) return;
        try {
            if (shader == null) return;

            Shader shaderInstance = shader.shader();
            if (shaderInstance == null) return;

            ShaderRegistryEntry shaderData = shaderInstance.getShaderData();
            if (shaderData == null) return;

            if (shaderData.isPhotosensitive() && disablePhotosensitivity) return;

            Callable<RenderLocation<?>> callableRenderLocation = shaderInstance.getRenderLocation();
            if (callableRenderLocation == null) return;

            RenderLocation<?> renderLocation = callableRenderLocation.call();
            if (renderLocation == null) return;

            boolean isFallback = type.equals(getFallback(shaderInstance.getUseFabulous()));

            boolean isCorrectType = renderLocation.equals(type);
            if (!isCorrectType && !isFallback) return;

            boolean shouldFallback = (shaderInstance.getUseFabulous() && !renderLocation.isFabulousSupported()) || (shaderInstance.getUseDepth() && !renderLocation.isDepthSupported()) || (shaderData.useFallbackWhenOverUi() && renderLocation.uiType().equals(UIType.OVER)) || (shaderData.useFallbackWhenUnderUi() && renderLocation.uiType().equals(UIType.UNDER));
            if (shouldFallback && !renderLocation.canFallback()) return;

            boolean canRender = (!shouldFallback && isCorrectType) || (shouldFallback && isFallback);
            if (!canRender) return;

            type.render(id, shader, data);
        } catch (Exception error) {
            Data.getVersion().sendToLog(LogType.ERROR, "Failed to render {} shader with id: {}:{}", type.identifier(), id, error);
        }
    }

    public static <T> RenderLocation<T> register(Identifier identifier, Renderer<T> renderer, boolean isDepthSupported, UIType uiType, boolean canFallback) {
        return register(identifier, renderer, isDepthSupported, uiType, false, canFallback);
    }

    public static <T> RenderLocation<T> register(Identifier identifier, Renderer<T> renderer, boolean isDepthSupported, UIType uiType, boolean canUseFabulous, boolean canFallback) {
        RenderLocation<T> renderLocation = new RenderLocation<>(identifier, renderer, isDepthSupported, uiType, canUseFabulous, canFallback);
        Events.RenderLocation.register(identifier, renderLocation);
        return renderLocation;
    }

    public interface Renderer<T> {
        void render(Identifier id, Shader.Data shader, T Data);
    }

    public record RenderLocation<T>(Identifier identifier, Renderer<T> renderer, boolean isDepthSupported, UIType uiType, boolean isFabulousSupported, boolean canFallback) {
        public void render(Identifier id, Shader.Data shader, T data) {
            this.renderer().render(id, shader, data);
        }
    }

    public enum UIType {
        NONE,
        UNDER,
        OVER
    }
}
