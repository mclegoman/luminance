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
import net.minecraft.client.render.DefaultFramebufferSet;
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
		void run(FrameGraphBuilder builder, int textureWidth, int textureHeight, DefaultFramebufferSet framebufferSet);
	}
	public interface GameRender {
		void run(Framebuffer framebuffer, ObjectAllocator objectAllocator);
	}
	public interface GeneralRender {
		void run(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet);

		static void fromWorldRender(GeneralRender generalRender, FrameGraphBuilder builder, int textureWidth, int textureHeight, DefaultFramebufferSet framebufferSet) {
			generalRender.run(builder, textureWidth, textureHeight, framebufferSet);
		}

		static void fromGameRender(GeneralRender generalRender, Framebuffer framebuffer, ObjectAllocator objectAllocator) {
			FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();

			PostEffectProcessor.FramebufferSet framebufferSet = new DefaultableFramebufferSet(frameGraphBuilder, framebuffer);
			//PostEffectProcessor.FramebufferSet framebufferSet = PostEffectProcessor.FramebufferSet.singleton(PostEffectProcessor.MAIN, frameGraphBuilder.createObjectNode("main", framebuffer));

			generalRender.run(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
			frameGraphBuilder.run(objectAllocator);
		}
	}
}