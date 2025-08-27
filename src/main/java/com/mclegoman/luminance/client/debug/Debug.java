/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.debug;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.Couple;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Debug {
	public static Couple<Identifier, Identifier> debugShader;
	public static boolean debugShaderEnabled;
	public static Shader.RenderType debugRenderType;
	public static void cycleDebugRenderType() {
		switch (Debug.debugRenderType) {
			case UI -> Debug.debugRenderType = Shader.RenderType.WORLD;
			case WORLD -> Debug.debugRenderType = Shader.RenderType.UI_BACKGROUND;
			case UI_BACKGROUND -> Debug.debugRenderType = Shader.RenderType.PANORAMA;
			case PANORAMA -> Debug.debugRenderType = Shader.RenderType.UI;
		}
	}
	public static void applyDebugShader() {
		if (ClientData.isDevelopment()) {
			Events.ShaderRender.register(getDebugId(), new ArrayList<>());
			Events.ShaderRender.modify(getDebugId(), List.of(new Shader.Data(getDebugId(0), new Shader(Shaders.get(Debug.debugShader.getFirst(), Debug.debugShader.getSecond()), () -> Debug.debugRenderType, () -> Debug.debugShaderEnabled))));
		}
	}
	public static void setDebugShader(Identifier registry, Identifier shader) {
		if (ClientData.isDevelopment()) {
			try {
				Debug.debugShader.setFirst(registry);
				Debug.debugShader.setSecond(shader);
				applyDebugShader();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set debug shader: {}", error));
				Debug.debugShader.setFirst(Shaders.getMainRegistryId());
				Debug.debugShader.setSecond(Identifier.of("box_blur"));
				applyDebugShader();
			}
		}
	}
	public static Identifier getDebugId() {
		return Identifier.of(Data.getVersion().getID(), "debug");
	}
	public static Identifier getDebugId(int index) {
		return Identifier.of(Data.getVersion().getID() + "_debug", String.valueOf(index));
	}
	static {
		debugShader = new Couple<>(Shaders.getMainRegistryId(), Identifier.of("box_blur"));
		debugShaderEnabled = false;
		debugRenderType = Shader.RenderType.WORLD;
	}
}
