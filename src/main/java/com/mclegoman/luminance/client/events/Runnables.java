/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.LuminanceTargetBundle;
import com.mclegoman.luminance.client.shaders.ShaderRegistryEntry;
import dev.dannytaylor.perspective.seam.client.events.SeamClientRunnables;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.DeltaTracker;
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

		static void fromGameData(LevelRender levelRender, SeamClientRunnables.RenderData data) {
			FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
			PostChain.TargetBundle targetBundle = LuminanceTargetBundle.create(frameGraphBuilder, data.renderTarget());
			levelRender.run(new Data(frameGraphBuilder, data.renderTarget().width, data.renderTarget().height, targetBundle));
			frameGraphBuilder.execute(data.resourceAllocator());
		}

		record Data(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle) {}
	}
}