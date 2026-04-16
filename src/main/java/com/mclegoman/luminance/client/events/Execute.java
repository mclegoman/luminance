/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.LuminanceTargetBundle;
import com.mclegoman.luminance.client.shaders.RenderLocations;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class Execute {
	public static void registerClientResourceReloaders(ReloadableResourceManager resourceManager) {
		Events.ClientResourceReloaders.registry.forEach((id, resourceReloader) -> resourceManager.registerReloadListener(resourceReloader));
	}

	public static void afterClientResourceReload() {
		Events.AfterClientResourceReload.registry.forEach((id, runnable) -> runnable.run());
		if (ClientData.minecraft.getCameraEntity() != null) {
			SpectatorHandler.onSpectate(ClientData.minecraft.getCameraEntity(), LuminanceConfig.config.spectatorPriorityMode.value().getMode());
		}
	}

	public static void onCameraEntitySet(@NotNull Entity entity) {
		SpectatorHandler.onSpectate(entity, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
	}

	public static void onJoinWorld() {
		ClientData.minecraft.schedule(() -> {
			assert ClientData.minecraft.player != null;
			SpectatorHandler.onSpectate(ClientData.minecraft.player, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
		});
	}

	public static void onDisconnect() {
		SpectatorHandler.clearActive();
	}

	public static void beforeInGameHudRender(GuiGraphics context, DeltaTracker renderTickCounter) {
		ShaderTime.currentRenderLocation = RenderLocations.UI;
		Events.BeforeInGameHudRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(context, renderTickCounter);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterInGameHudRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterInGameHudRender(GuiGraphics context, DeltaTracker renderTickCounter) {
		Events.AfterInGameHudRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(context, renderTickCounter);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterInGameHudRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void beforeGameRender() {
		ShaderTime.currentRenderLocation = RenderLocations.WORLD;
		Events.BeforeGameRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterGameRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterVanillaPostEffectRender(GraphicsResourceAllocator allocator) {
		DepthFix.mergeDepth(allocator);

		Events.AfterVanillaPostEffectRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.GameRender.Data(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute VanillaPostEffect event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterUiRender(GraphicsResourceAllocator allocator) {
		Events.AfterUiRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.GameRender.Data(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterGameRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void beforeUiRender(GraphicsResourceAllocator allocator) {
		Events.BeforeUiRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.GameRender.Data(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute BeforeUiRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterUiBackgroundRender(GraphicsResourceAllocator allocator) {
		RenderLocations.RenderLocation<?> previous = ShaderTime.currentRenderLocation;
		ShaderTime.currentRenderLocation = RenderLocations.UI_BACKGROUND;
		Events.AfterUiBackgroundRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.GameRender.Data(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterUiBackgroundRender event with id: {}: {}", id, error);
			}
		}));
		// this and afterPanoramaRender are a special case, so resetting the RenderLocation it makes sense
		ShaderTime.currentRenderLocation = previous;
	}

	public static void afterPanoramaRender(GraphicsResourceAllocator allocator) {
		RenderLocations.RenderLocation<?> previous = ShaderTime.currentRenderLocation;
		ShaderTime.currentRenderLocation = RenderLocations.PANORAMA;
		Events.AfterPanoramaRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.GameRender.Data(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterPanoramaRender event with id: {}: {}", id, error);
			}
		}));
		ShaderTime.currentRenderLocation = previous;
	}

	public static void resize(int width, int height) {
		Events.OnResized.registry.forEach((id, runnable) -> runnable.run(width, height));
	}

	public static void beforeLevelRender() {
		ShaderTime.currentRenderLocation = RenderLocations.LEVEL;
		Events.BeforeLevelRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute BeforeWorldRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterFabulousRender(FrameGraphBuilder frameGraphBuilder, LevelTargetBundle levelTargetBundle, RenderTargetDescriptor renderTargetDescriptor) {
		if (Events.AfterFabulousRender.registry.isEmpty()) {
			return;
		}

		PostChain.TargetBundle targetBundle = LuminanceTargetBundle.createIfAbsent(frameGraphBuilder, levelTargetBundle, renderTargetDescriptor);
		Events.AfterFabulousRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.LevelRender.Data(frameGraphBuilder, ClientData.minecraft.getMainRenderTarget().width, ClientData.minecraft.getMainRenderTarget().height, targetBundle));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterFabulousRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterLevelRender(GraphicsResourceAllocator allocator) {
		Events.AfterLevelRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new Runnables.GameRender.Data(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterWorldRender event with id: {}: {}", id, error);
			}
		}));

		DepthFix.copyDepth(allocator);
	}

	public static void beforeShaderRender(PostPass postEffectPass) {
		Events.BeforeShaderRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(postEffectPass);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute BeforeShaderRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterShaderRender(PostPass postEffectPass) {
		Events.AfterShaderRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(postEffectPass);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterShaderRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static boolean onMouseScroll(long windowHandle, double horizontal, double vertical, Vector2i scroll) {
		boolean shouldCancel = false;
		for (Identifier registry : Events.OnMouseScroll.registry.keySet()) {
			if (Events.OnMouseScroll.get(registry).call(windowHandle, horizontal, vertical, scroll)) shouldCancel = true;
		}
		return shouldCancel;
	}

	public static boolean onMouseButton(long windowHandle, MouseButtonInfo mouseButtonInfo, @MouseButtonInfo.Action int action) {
		boolean shouldCancel = false;
		for (Identifier registry : Events.OnMouseButton.registry.keySet()) {
			if (Events.OnMouseButton.get(registry).call(windowHandle, mouseButtonInfo, action)) shouldCancel = true;
		}
		return shouldCancel;
	}
}
