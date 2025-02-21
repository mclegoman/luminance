/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.debug;

import com.mclegoman.luminance.client.shaders.Shader;

public class Debug {
	public static boolean debugShader;
	public static Shader.RenderType debugRenderType;
	public static void cycleDebugRenderType() {
		switch (Debug.debugRenderType) {
			case GAME -> Debug.debugRenderType = Shader.RenderType.WORLD;
			case WORLD -> Debug.debugRenderType = Shader.RenderType.SCREEN_BACKGROUND;
			case SCREEN_BACKGROUND -> Debug.debugRenderType = Shader.RenderType.PANORAMA;
			case PANORAMA -> Debug.debugRenderType = Shader.RenderType.GAME;
		}
	}
	static {
		debugShader = false;
		debugRenderType = Shader.RenderType.WORLD;
	}
}
