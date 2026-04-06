/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.RenderLocations;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

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
		mergeDepth(allocator);

		Events.AfterVanillaPostEffectRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute VanillaPostEffect event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterUiRender(GraphicsResourceAllocator allocator) {
		Events.AfterUiRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterGameRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void beforeUiRender(GraphicsResourceAllocator allocator) {
		Events.BeforeUiRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute BeforeUiRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterUiBackgroundRender(GraphicsResourceAllocator allocator) {
		RenderLocations.RenderLocation previous = ShaderTime.currentRenderLocation;
		ShaderTime.currentRenderLocation = RenderLocations.UI_BACKGROUND;
		Events.AfterUiBackgroundRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterUiBackgroundRender event with id: {}: {}", id, error);
			}
		}));
		// this and afterPanoramaRender are a special case, so resetting the RenderLocation it makes sense
		ShaderTime.currentRenderLocation = previous;
	}

	public static void afterPanoramaRender(GraphicsResourceAllocator allocator) {
		RenderLocations.RenderLocation previous = ShaderTime.currentRenderLocation;
		ShaderTime.currentRenderLocation = RenderLocations.PANORAMA;
		Events.AfterPanoramaRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterPanoramaRender event with id: {}: {}", id, error);
			}
		}));
		ShaderTime.currentRenderLocation = previous;
	}

	public static void resize(int width, int height) {
		Events.OnResized.registry.forEach((id, runnable) -> runnable.run(width, height));
	}

	public static void beforeWorldRender() {
		ShaderTime.currentRenderLocation = RenderLocations.WORLD;
		Events.BeforeWorldRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute BeforeWorldRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterFabulousRender(FrameGraphBuilder frameGraphBuilder, PostChain.TargetBundle targetBundle) {
		if (Events.AfterFabulousRender.registry.isEmpty()) {
			return;
		}

		Events.AfterFabulousRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(frameGraphBuilder, ClientData.minecraft.getMainRenderTarget().width, ClientData.minecraft.getMainRenderTarget().height, targetBundle);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterFabulousRender event with id: {}: {}", id, error);
			}
		}));
	}

	public static void afterWorldRender(GraphicsResourceAllocator allocator) {
		Events.AfterWorldRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterWorldRender event with id: {}: {}", id, error);
			}
		}));

		copyDepth(allocator);
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

	private static RenderTargetDescriptor targetDescriptor;
	private static RenderTarget worldDepth;

	private static final RenderPipeline depthPipeline = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
			.withDepthWrite(true) // post-processing snippet has depth write off
			.withFragmentShader(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "depth_fix"))
			.withVertexShader(Identifier.withDefaultNamespace("core/screenquad"))
			.withLocation(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "depth_fix"))
			.build();

	private static void copyDepth(GraphicsResourceAllocator allocator) {
		cleanupDepth(allocator);

		if (CompatHelper.isIrisShadersEnabled()) {
			return;
        }

		RenderTarget target = ClientData.minecraft.getMainRenderTarget();

		targetDescriptor = new RenderTargetDescriptor(target.width, target.height, true, 0);
		worldDepth = allocator.acquire(targetDescriptor);
		worldDepth.copyDepthFrom(target);
	}

	private static void mergeDepth(GraphicsResourceAllocator allocator) {
		if (CompatHelper.isIrisShadersEnabled() || worldDepth == null) {
			return;
		}



		try {
			RenderTarget target = ClientData.minecraft.getMainRenderTarget();

			// temporary fix for depth, just ignoring hand
			target.copyDepthFrom(worldDepth);

			// attempt at a proper fix - it correctly renders the shader
			// but the texture bind doesnt seem to be doing anything
			// and it also cant write to the depth buffer
			// TODO: merge the hand depth nicely

			/*
			CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
			RenderSystem.backupProjectionMatrix();

			CachedOrthoProjectionMatrixBuffer matrixCache = ((ShaderManagerAccessor)ClientData.minecraft.getShaderManager()).getPostChainProjectionMatrixBuffer();
			RenderSystem.setProjectionMatrix(matrixCache.getBuffer(target.width, target.height), ProjectionType.ORTHOGRAPHIC);

			try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Depth Merge", target.getColorTextureView(), OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
				renderPass.setPipeline(depthPipeline);

				GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
				renderPass.bindTexture("InSampler", worldDepth.getDepthTextureView(), sampler);
				renderPass.bindTexture("HandSampler", target.getDepthTextureView(), sampler);

				//GL11.glDepthFunc(GL11.GL_ALWAYS);
				//GL11.glEnable(GL11.GL_DEPTH_TEST);

				renderPass.draw(0, 3);

				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				//GL11.glDepthFunc(GL11.GL_LESS);
            }

			RenderSystem.restoreProjectionMatrix();
			*/
		} catch (Exception e) {
			Data.getVersion().sendToLog(LogType.INFO, "Error Fixing Depth: "+e.getMessage());
		}

		cleanupDepth(allocator);
	}

	private static void cleanupDepth(GraphicsResourceAllocator allocator) {
		if (worldDepth != null) {
		 	allocator.release(targetDescriptor, worldDepth);
		 	worldDepth = null;
		}
	}
}
