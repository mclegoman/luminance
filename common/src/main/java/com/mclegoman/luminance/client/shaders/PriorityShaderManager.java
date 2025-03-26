/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import net.minecraft.entity.Entity;

public interface PriorityShaderManager {
	int getPriority();
	void set(Entity entity);
	void clear();
}
