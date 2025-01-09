/*
    Luminance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.ShaderRegistry;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ObjectAllocator;

public class Runnables {
	public interface InGameHudRender {
		default void run(DrawContext context, RenderTickCounter renderTickCounter) {}
	}
	public interface Shader {
		default void run(PostEffectPass postEffectPass) {}
	}
	public interface ShaderData {
		default void run(ShaderRegistry shaderData) {}
	}
	public interface OnResized {
		default void run(int width, int height) {}
	}
	public interface WorldRender {
		default void run(FrameGraphBuilder builder, int textureWidth, int textureHeight, DefaultFramebufferSet framebufferSet) {}
	}
	public interface GameRender {
		default void run(Framebuffer framebuffer, ObjectAllocator objectAllocator) {}
	}
}