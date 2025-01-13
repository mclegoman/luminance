/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import java.util.Optional;

public interface Uniform {
	float get();

	void tick();
	void update(float tickDelta, float deltaTime);

	Optional<Float> getMin();
	Optional<Float> getMax();
}
