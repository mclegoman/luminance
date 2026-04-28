/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.LuminanceTargetBundle;
import com.mclegoman.luminance.client.shaders.RenderLocations;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.dannytaylor.perspective.seam.client.events.SeamClientRunnables;
import dev.dannytaylor.perspective.seam.common.data.log.SeamLog;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;

public class Execute {
	public static void onCameraEntitySet(Entity entity) {
		SpectatorHandler.onSpectate(entity, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
	}

	public static void onJoinWorld() {
		ClientData.minecraft.schedule(() -> {
			onCameraEntitySet(ClientData.minecraft.player);
		});
	}

	public static void onDisconnect() {
		SpectatorHandler.clearActive();
	}

	public static void beforeLevelRender() {
		ShaderTime.currentRenderLocation = RenderLocations.LEVEL;
		Events.BeforeLevelRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				SeamLog.error(LuminanceClient.getMod(), "Failed to execute BeforeWorldRender event with id: {}", id, error);
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
				SeamLog.error(LuminanceClient.getMod(), "Failed to execute AfterFabulousRender event with id: {}", id, error);
			}
		}));
	}

	public static void afterLevelRender(GraphicsResourceAllocator allocator) {
		Events.AfterLevelRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(new SeamClientRunnables.RenderData(ClientData.minecraft.getMainRenderTarget(), allocator));
			} catch (Exception error) {
				SeamLog.error(LuminanceClient.getMod(), "Failed to execute AfterWorldRender event with id: {}", id, error);
			}
		}));

		DepthFix.copyDepth(allocator);
	}

	public static void beforeShaderRender(PostPass postEffectPass) {
		Events.BeforeShaderRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(postEffectPass);
			} catch (Exception error) {
				SeamLog.error(LuminanceClient.getMod(), "Failed to execute BeforeShaderRender event with id: {}", id, error);
			}
		}));
	}

	public static void afterShaderRender(PostPass postEffectPass) {
		Events.AfterShaderRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(postEffectPass);
			} catch (Exception error) {
				SeamLog.error(LuminanceClient.getMod(), "Failed to execute AfterShaderRender event with id: {}", id, error);
			}
		}));
	}
}
