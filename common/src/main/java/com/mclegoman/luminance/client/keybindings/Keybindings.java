/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.keybindings;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.screen.config.ConfigScreen;
import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.DateHelper;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Keybindings {
	public static final KeyBinding adjustAlpha;
	public static final KeyBinding openConfig;
	public static KeyBinding debug;
	public static KeyBinding debug_2;
	public static final List<KeyBinding> allKeybindings = new ArrayList<>();
	static {
		allKeybindings.add(adjustAlpha = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "adjust_alpha", GLFW.GLFW_KEY_J));
		allKeybindings.add(openConfig = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "open_config", GLFW.GLFW_KEY_UNKNOWN));
		if (ClientData.isDevelopment) {
			allKeybindings.add(debug = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "debug", GLFW.GLFW_KEY_UNKNOWN));
			allKeybindings.add(debug_2 = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "debug_2", GLFW.GLFW_KEY_UNKNOWN));
		} else {
			debug = null;
			debug_2 = null;
		}
	}
	public static void init() {
		Data.getVersion().sendToLog(LogType.INFO, Translation.getString("Initializing keybindings!"));
	}
	public static void tick() {
		if (openConfig.wasPressed()) {
			ClientData.minecraft.setScreen(new ConfigScreen(ClientData.minecraft.currentScreen, false, DateHelper.isPride()));
		}
		if (ClientData.isDevelopment) {
			if (debug != null && debug.wasPressed()) Debug.debugShader = !Debug.debugShader;
			if (debug_2 != null && debug_2.wasPressed()) {
				switch (Debug.debugRenderType) {
					case GAME -> Debug.debugRenderType = Shader.RenderType.WORLD;
					case WORLD -> Debug.debugRenderType = Shader.RenderType.GAME;
				}
			}
		}
	}
}