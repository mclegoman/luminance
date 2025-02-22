/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.util.Identifier;

public class Execute {
	public static void registerClientResourceReloaders(ReloadableResourceManagerImpl resourceManager) {
		Events.ClientResourceReload.registry.forEach((id, resourceReloader) -> resourceManager.registerReloader(resourceReloader));
	}
	public static void beforeInGameHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
		Events.BeforeInGameHudRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(context, renderTickCounter);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterInGameHudRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterInGameHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
		Events.AfterInGameHudRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(context, renderTickCounter);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterInGameHudRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void beforeGameRender() {
		Events.BeforeGameRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterGameRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterHandRender(ObjectAllocator allocator) {
		RenderSystem.depthMask(false);
		Events.AfterHandRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterHandRender event with id: {}: {}", id, error));
			}
		}));
		RenderSystem.depthMask(true);
	}
	public static void afterGameRender(ObjectAllocator allocator) {
		Events.AfterGameRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterGameRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterScreenBackgroundRender(ObjectAllocator allocator) {
		Events.AfterScreenBackgroundRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterScreenBackgroundRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterPanoramaRender(ObjectAllocator allocator) {
		Events.AfterPanoramaRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterPanoramaRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void resize(int width, int height) {
		Events.OnResized.registry.forEach((id, runnable) -> runnable.run(width, height));
	}
	public static void beforeWorldRender() {
		Events.BeforeWorldRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute BeforeWorldRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterWeatherRender(FrameGraphBuilder frameGraphBuilder, PostEffectProcessor.FramebufferSet framebufferSet) {
		if (Events.AfterWeatherRender.registry.isEmpty()) {
			return;
		}

		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "prepare_shader_render"), () -> RenderSystem.depthMask(false));
		Events.AfterWeatherRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(frameGraphBuilder, ClientData.minecraft.getFramebuffer().textureWidth, ClientData.minecraft.getFramebuffer().textureHeight, framebufferSet);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterWeatherRender event with id: {}: {}", id, error));
			}
		}));
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "cleanup_shader_render"), () -> RenderSystem.depthMask(true));
	}
	public static void afterWorldRender(ObjectAllocator allocator) {
		Events.AfterWorldRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterWorldRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void beforeShaderRender(PostEffectPass postEffectPass) {
		Events.BeforeShaderRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(postEffectPass);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute BeforeShaderRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterShaderRender(PostEffectPass postEffectPass) {
		Events.AfterShaderRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(postEffectPass);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterShaderRender event with id: {}: {}", id, error));
			}
		}));
	}
}
