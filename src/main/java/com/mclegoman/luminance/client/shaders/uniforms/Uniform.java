/*
    Luminance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import java.util.Optional;

public interface Uniform {
	float get();
	float getPrev();
	float getDelta();
	float getSmooth(float tickDelta);
	float getSmoothPrev();
	float getSmoothDelta();

	void tick(float tickDelta);
	void call(float tickDelta) throws Exception;

	Optional<Float> getMin();
	Optional<Float> getMax();
}
