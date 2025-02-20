/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.DefaultableFramebufferSet;
import com.mclegoman.luminance.client.shaders.ShaderRegistryEntry;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ObjectAllocator;

public class Runnables {
	public interface InGameHudRender {
		void run(DrawContext context, RenderTickCounter renderTickCounter);
	}
	public interface Shader {
		void run(PostEffectPass postEffectPass);
	}
	public interface ShaderData {
		void run(ShaderRegistryEntry shaderData);
	}
	public interface OnResized {
		void run(int width, int height);
	}
	public interface WorldRender {
		void run(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet);

		static void fromGameRender(WorldRender worldRender, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
			FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
			PostEffectProcessor.FramebufferSet framebufferSet = new DefaultableFramebufferSet(frameGraphBuilder, framebuffer, DefaultableFramebufferSet.fabulous);
			worldRender.run(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
			frameGraphBuilder.run(objectAllocator);
		}
	}
	public interface GameRender {
		void run(Framebuffer framebuffer, ObjectAllocator objectAllocator);
	}
}