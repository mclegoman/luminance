/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.shaders.interfaces.FramePassInterface;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class Execute {
	public static void registerClientResourceReloaders(ReloadableResourceManagerImpl resourceManager) {
		Events.ClientResourceReloaders.registry.forEach((id, resourceReloader) -> resourceManager.registerReloader(resourceReloader));
	}
	public static void afterClientResourceReload() {
		Events.AfterClientResourceReload.registry.forEach((id, runnable) -> runnable.run());
		if (ClientData.minecraft.cameraEntity != null) {
			SpectatorHandler.onSpectate(ClientData.minecraft.cameraEntity, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
		}
	}
	public static void onCameraEntitySet(@NotNull Entity entity) {
		SpectatorHandler.onSpectate(entity, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
	}
	public static void onJoinWorld() {
		ClientData.minecraft.send(() -> {
			assert ClientData.minecraft.player != null;
			SpectatorHandler.onSpectate(ClientData.minecraft.player, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
		});
	}
	public static void onDisconnect() {
		SpectatorHandler.clearActive();
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
	public static void afterVanillaPostEffectRender(ObjectAllocator allocator) {
		mergeDepth(allocator);

		RenderSystem.depthMask(false);
		Events.AfterVanillaPostEffectRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute VanillaPostEffect event with id: {}: {}", id, error));
			}
		}));
		RenderSystem.depthMask(true);
	}
	public static void afterUiRender(ObjectAllocator allocator) {
		Events.AfterUiRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(ClientData.minecraft.getFramebuffer(), allocator);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterGameRender event with id: {}: {}", id, error));
			}
		}));
	}
	public static void afterScreenBackgroundRender(ObjectAllocator allocator) {
		Events.AfterUiBackgroundRender.registry.forEach(((id, runnable) -> {
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
	public static void afterFabulousRender(FrameGraphBuilder frameGraphBuilder, PostEffectProcessor.FramebufferSet framebufferSet) {
		if (Events.AfterFabulousRender.registry.isEmpty()) {
			return;
		}

		FramePassInterface.createForcedPass(frameGraphBuilder, Identifier.of(Data.getVersion().getID(), "prepare_shader_render"), () -> RenderSystem.depthMask(false));
		Events.AfterFabulousRender.registry.forEach(((id, runnable) -> {
			try {
				runnable.run(frameGraphBuilder, ClientData.minecraft.getFramebuffer().textureWidth, ClientData.minecraft.getFramebuffer().textureHeight, framebufferSet);
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to execute AfterFabulousRender event with id: {}: {}", id, error));
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

		copyDepth(allocator);
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

	private static SimpleFramebufferFactory framebufferFactory;
	private static Framebuffer worldDepth;

	private static void copyDepth(ObjectAllocator allocator) {
		cleanupDepth(allocator);

		if (CompatHelper.isIrisShadersEnabled()) {
			return;
		}

		Framebuffer framebuffer = ClientData.minecraft.getFramebuffer();

		framebufferFactory = new SimpleFramebufferFactory(framebuffer.textureWidth, framebuffer.textureHeight, true);
		worldDepth = allocator.acquire(framebufferFactory);
		worldDepth.copyDepthFrom(framebuffer);

		framebuffer.beginWrite(false);
	}

	private static void mergeDepth(ObjectAllocator allocator) {
		if (CompatHelper.isIrisShadersEnabled()) {
			return;
		}

		try {
			Framebuffer framebuffer = ClientData.minecraft.getFramebuffer();
			framebuffer.beginWrite(true);

			ShaderProgram shaderProgram = ClientData.minecraft.getShaderLoader().getProgramToLoad(new ShaderProgramKey(Identifier.of(Data.getVersion().getID(), "depth_fix"), VertexFormats.POSITION, Defines.EMPTY));
			shaderProgram.addSamplerTexture("InSampler", worldDepth.getDepthAttachment());
			shaderProgram.addSamplerTexture("HandSampler", framebuffer.getDepthAttachment());
			shaderProgram.getUniformOrDefault("InSize").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
			shaderProgram.getUniformOrDefault("OutSize").set((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
			RenderSystem.setShader(shaderProgram);

			RenderSystem.depthFunc(519);
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);

			Matrix4f projectionMatrix = (new Matrix4f()).setOrtho(0.0F, (float)framebuffer.textureWidth, 0.0F, (float)framebuffer.textureHeight, 0.1F, 1000.0F);

			RenderSystem.backupProjectionMatrix();
			RenderSystem.setProjectionMatrix(projectionMatrix, ProjectionType.ORTHOGRAPHIC);
			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
			bufferBuilder.vertex(0.0F, 0.0F, 500.0F);
			bufferBuilder.vertex((float)framebuffer.textureWidth, 0.0F, 500.0F);
			bufferBuilder.vertex((float)framebuffer.textureWidth, (float)framebuffer.textureHeight, 500.0F);
			bufferBuilder.vertex(0.0F, (float)framebuffer.textureHeight, 500.0F);
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			RenderSystem.restoreProjectionMatrix();

			framebuffer.endWrite();
		} catch (Exception e) {
			Data.getVersion().sendToLog(LogType.INFO, "Error Fixing Depth: "+e.getMessage());
		}

		cleanupDepth(allocator);
	}

	private static void cleanupDepth(ObjectAllocator allocator) {
		if (worldDepth != null) {
			allocator.release(framebufferFactory, worldDepth);
			worldDepth = null;
		}
	}
}
