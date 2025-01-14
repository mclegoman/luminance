/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RootUniform extends TreeUniform {
	protected final Callables.ShaderRender callable;
	@Nullable private final UniformValue min;
	@Nullable private final UniformValue max;

	protected UniformValue value;

	public RootUniform(String name, Callables.ShaderRender callable, int length) {
		this(name, callable, length, null, null);
	}
	public RootUniform(String name, Callables.ShaderRender callable, int length, @Nullable UniformValue min, @Nullable UniformValue max) {
		super(name);
		this.callable = callable;
		this.min = min;
		this.max = max;

		this.value = new UniformValue(length);
	}

	@Override
	public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
		return this.value;
	}

	@Override
	public int getLength() {
		return value.values.size();
	}

	@Override
	public void preParentUpdate(ShaderTime shaderTime) {

	}

	@Override
	public void updateValue(ShaderTime shaderTime) {
		call(shaderTime);
	}

	@Override
	public void onRegister(String fullName) {

	}

	protected void call(ShaderTime shaderTime) {
		this.callable.call(shaderTime, value);
		getMin().ifPresent(value::max);
        getMax().ifPresent(value::min);
	}

	@Override
	public Optional<UniformValue> getMin() {
		return Optional.ofNullable(min);
	}

	@Override
	public Optional<UniformValue> getMax() {
		return Optional.ofNullable(max);
	}
}
