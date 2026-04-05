/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.RenderTypes;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

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
		ShaderTime.currentRenderType = RenderTypes.UI;
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
		ShaderTime.currentRenderType = RenderTypes.WORLD;
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

		// direct GL call to replace RenderSystem.depthMask. not sure if theres an api better alternative
		// vulkan seems to do it by having a separate pipeline? perhaps the pipeline used for shaders can have their depth disabled?

		// TODO: see if this has been done already

		GL11.glDepthMask(false);
		Events.AfterVanillaPostEffectRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute VanillaPostEffect event with id: {}: {}", id, error);
			}
		}));
		GL11.glDepthMask(true);
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
		RenderTypes.RenderType previous = ShaderTime.currentRenderType;
		ShaderTime.currentRenderType = RenderTypes.UI_BACKGROUND;
		Events.AfterUiBackgroundRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterUiBackgroundRender event with id: {}: {}", id, error);
			}
		}));
		// this and afterPanoramaRender are a special case, so resetting the RenderType it makes sense
		ShaderTime.currentRenderType = previous;
	}
	public static void afterPanoramaRender(GraphicsResourceAllocator allocator) {
		RenderTypes.RenderType previous = ShaderTime.currentRenderType;
		ShaderTime.currentRenderType = RenderTypes.PANORAMA;
		Events.AfterPanoramaRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getMainRenderTarget(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterPanoramaRender event with id: {}: {}", id, error);
			}
		}));
		ShaderTime.currentRenderType = previous;
	}
	public static void resize(int width, int height) {
		Events.OnResized.registry.forEach((id, runnable) -> runnable.run(width, height));
	}
	public static void beforeWorldRender() {
		ShaderTime.currentRenderType = RenderTypes.WORLD;
		Events.BeforeWorldRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute BeforeWorldRender event with id: {}: {}", id, error);
			}
		}));
	}
	public static void afterFabulousRender(FrameGraphBuilder frameGraphBuilder, PostChain.TargetBundle framebufferSet) {
		if (Events.AfterFabulousRender.registry.isEmpty()) {
			return;
		}

		// see Execute.afterVanillaPostEffectRender() for note on depth mask
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "prepare_shader_render"), () -> GL11.glDepthMask(false));
		Events.AfterFabulousRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(frameGraphBuilder, ClientData.minecraft.getMainRenderTarget().width, ClientData.minecraft.getMainRenderTarget().height, framebufferSet);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterFabulousRender event with id: {}: {}", id, error);
			}
		}));
		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "cleanup_shader_render"), () -> GL11.glDepthMask(true));
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

	//private static SimpleFramebufferFactory framebufferFactory;
	//private static Framebuffer worldDepth;

	private static void copyDepth(GraphicsResourceAllocator allocator) {
		cleanupDepth(allocator);

		if (CompatHelper.isIrisShadersEnabled()) {
        }

		// TODO: update this (its used for depth merging)
		//  beginWrite no longer exists, it might be fine to just ignore though, it might rebind the correct buffer as is

		//Framebuffer framebuffer = ClientData.minecraft.getFramebuffer();

		//framebufferFactory = new SimpleFramebufferFactory(framebuffer.textureWidth, framebuffer.textureHeight, true, 0);
		//worldDepth = allocator.acquire(framebufferFactory);
		//worldDepth.copyDepthFrom(framebuffer);

		//framebuffer.beginWrite(false);
	}

	private static void mergeDepth(GraphicsResourceAllocator allocator) {
		if (CompatHelper.isIrisShadersEnabled()) {
			return;
		}

		try {
			// TODO: update this
			//  i dont remember how i went around creating this
			//  i think i fully reverse engineered how shader loading works myself

//			Framebuffer framebuffer = ClientData.minecraft.getFramebuffer();
//			framebuffer.beginWrite(true);
//
//			ShaderProgram shaderProgram = ClientData.minecraft.getShaderLoader().getProgramToLoad(new ShaderProgramKey(Identifier.of(Data.getVersion().getID(), "depth_fix"), VertexFormats.POSITION, Defines.EMPTY));
//			shaderProgram.addSamplerTexture("InSampler", worldDepth.getDepthAttachment());
//			shaderProgram.addSamplerTexture("HandSampler", framebuffer.getDepthAttachment());
//			shaderProgram.getUniformOrDefault("InSize").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
//			shaderProgram.getUniformOrDefault("OutSize").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
//			RenderSystem.setShader(shaderProgram);
//
//			RenderSystem.depthFunc(519);
//			RenderSystem.enableDepthTest();
//			RenderSystem.depthMask(true);
//
//			Matrix4f projectionMatrix = (new Matrix4f()).setOrtho(0.0F, (float)framebuffer.textureWidth, 0.0F, (float)framebuffer.textureHeight, 0.1F, 1000.0F);
//
//			RenderSystem.backupProjectionMatrix();
//			RenderSystem.setProjectionMatrix(projectionMatrix, ProjectionType.ORTHOGRAPHIC);
//			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
//			bufferBuilder.vertex(0.0F, 0.0F, 500.0F);
//			bufferBuilder.vertex((float)framebuffer.textureWidth, 0.0F, 500.0F);
//			bufferBuilder.vertex((float)framebuffer.textureWidth, (float)framebuffer.textureHeight, 500.0F);
//			bufferBuilder.vertex(0.0F, (float)framebuffer.textureHeight, 500.0F);
//			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
//			RenderSystem.restoreProjectionMatrix();
//
//			framebuffer.endWrite();
		} catch (Exception e) {
			Data.getVersion().sendToLog(LogType.INFO, "Error Fixing Depth: "+e.getMessage());
		}

		cleanupDepth(allocator);
	}

	private static void cleanupDepth(GraphicsResourceAllocator allocator) {
		// if (worldDepth != null) {
		// 	allocator.release(framebufferFactory, worldDepth);
		// 	worldDepth = null;
		// }
	}
}
