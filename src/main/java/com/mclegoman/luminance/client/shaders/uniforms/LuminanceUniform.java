/*
    Luminance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.events.Callables;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LuminanceUniform implements Uniform {
	private final Callables.ShaderRender<Float> callable;
	private float prev;
	private float current;
	private float prevSmooth;
	private float smooth;
	@Nullable private final Float min;
	@Nullable private final Float max;
	public LuminanceUniform(Callables.ShaderRender<Float> callable, @Nullable Float min, @Nullable Float max) {
		this.callable = callable;
		this.min = min;
		this.max = max;
	}
	public float get() {
		return this.current;
	}
	public float getPrev() {
		return this.prev;
	}
	public float getDelta() {
		return this.current - this.prev;
	}
	public float getSmooth(float delta) {
		return this.smooth;
	}
	public float getSmoothPrev() {
		return this.prevSmooth;
	}
	public float getSmoothDelta() {
		return this.smooth - this.prevSmooth;
	}
	public void tick(float delta) {
		this.prevSmooth = this.smooth;
		this.smooth = (this.prevSmooth + this.current) * 0.5F;
	}
	public void call(float delta) throws Exception {
		this.prev = this.current;

		float value = this.callable.call(delta);
		Optional<Float> min = getMin();
		if (min.isPresent() && value < min.get()) {
			value = min.get();
		}
		Optional<Float> max = getMax();
		if (max.isPresent() && value > max.get()) {
			value = max.get();
		}
		this.current = value;

		this.smooth = MathHelper.lerp(delta, this.prevSmooth, this.current);
	}

	@Override
	public Optional<Float> getMin() {
		return Optional.ofNullable(min);
	}

	@Override
	public Optional<Float> getMax() {
		return Optional.ofNullable(max);
	}
}
