/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.ShaderRegistryEntry;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformVector;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import net.minecraft.client.input.MouseButtonInfo;
import org.joml.Vector2i;

public class Callables {
	@FunctionalInterface
	public interface UniformCalculation {
		void call(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector);
	}

	@FunctionalInterface
	public interface SingleValueUniformCalculation {
		float call(ShaderTime shaderTime);

		default UniformCalculation convert() {
			return (config, shaderTime, uniformValue) -> uniformValue.set(0, call(shaderTime));
		}
	}

	public interface PriorityHandler<T> {
		int getPriority(T t);

		void apply(T t);

		void clear();
	}

	@FunctionalInterface
	public interface ShaderRegistryCaller {
		boolean call(ShaderRegistryEntry shaderRenderData);
	}

	@FunctionalInterface
	public interface OnMouseScroll {
		boolean call(long windowHandle, double horizontal, double vertical, Vector2i scroll);
	}

	@FunctionalInterface
	public interface OnMouseButton {
		boolean call(long windowHandle, MouseButtonInfo mouseButtonInfo, @MouseButtonInfo.Action int action);
	}
}