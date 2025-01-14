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
	@Nullable protected final Callables.UniformCalculation updateFunc;
	@Nullable private final UniformValue min;
	@Nullable private final UniformValue max;

	protected UniformValue value;

	public RootUniform(String name, @Nullable Callables.UniformCalculation updateFunc, int length, @Nullable UniformValue min, @Nullable UniformValue max) {
		super(name);
		this.updateFunc = updateFunc;
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
		if (this.updateFunc != null) {
			this.updateFunc.call(shaderTime, value);
			clampToRange();
		}
	}

	@Override
	public void onRegister(String fullName) {

	}

	protected void clampToRange() {
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
