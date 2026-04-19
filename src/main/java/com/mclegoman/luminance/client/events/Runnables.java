/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.LuminanceTargetBundle;
import com.mclegoman.luminance.client.shaders.ShaderRegistryEntry;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.resources.Identifier;

import java.util.List;

public class Runnables {
	public interface InGameHudRender {
		void run(GuiGraphics guiGraphics, DeltaTracker renderTickCounter);
	}

	public interface Shader {
		void run(PostPass postEffectPass);
	}

	public interface ShaderData {
		void run(ShaderRegistryEntry shaderData, List<Identifier> registries);
	}

	public interface OnResized {
		void run(int width, int height);
	}

	public interface LevelRender {
		void run(Data data);

		static void fromGameData(LevelRender levelRender, GameRender.Data data) {
			FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
			PostChain.TargetBundle targetBundle = LuminanceTargetBundle.create(frameGraphBuilder, data.renderTarget);
			levelRender.run(new Data(frameGraphBuilder, data.renderTarget.width, data.renderTarget.height, targetBundle));
			frameGraphBuilder.execute(data.resourceAllocator);
		}

		record Data(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle) { }
	}

	public interface GameRender {
		void run(Data data);

		record Data(RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator) { }
	}
}