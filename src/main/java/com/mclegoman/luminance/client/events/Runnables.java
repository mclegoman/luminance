/*
    Luminance
    Contributor(s): dannytaylor
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
		void run(GuiGraphics context, DeltaTracker renderTickCounter);
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
	public interface WorldRender {
		void run(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle);

		static void fromGameRender(WorldRender worldRender, RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator) {
			FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
			PostChain.TargetBundle targetBundle = new LuminanceTargetBundle(frameGraphBuilder, renderTarget, LuminanceTargetBundle.fabulous);
			worldRender.run(frameGraphBuilder, renderTarget.width, renderTarget.height, targetBundle);
			frameGraphBuilder.execute(resourceAllocator);
		}
	}
	public interface GameRender {
		void run(RenderTarget renderTarget, GraphicsResourceAllocator resourceAllocator);
	}
}