/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.mixin.client.shaders.GameRendererAccessor;

public class Accessors {
	private static GameRendererAccessor gameRenderer;
	public static GameRendererAccessor getGameRenderer() {
		if (gameRenderer == null) {
			if (ClientData.minecraft.gameRenderer != null) gameRenderer = (GameRendererAccessor) ClientData.minecraft.gameRenderer;
		}
		return gameRenderer;
	}
}
