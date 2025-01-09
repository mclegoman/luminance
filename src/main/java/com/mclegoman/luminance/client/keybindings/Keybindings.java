/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
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

public class Keybindings {
	public static final KeyBinding adjustAlpha;
	public static final KeyBinding openConfig;
	public static final KeyBinding debug;
	public static final KeyBinding debug_2;
	public static final KeyBinding[] allKeybindings;
	static {
		allKeybindings = new KeyBinding[]{
				adjustAlpha = KeybindingHelper.getKeybinding(Data.version.getID(), Data.version.getID(), "adjust_alpha", GLFW.GLFW_KEY_J),
				openConfig = KeybindingHelper.getKeybinding(Data.version.getID(), Data.version.getID(), "open_config", GLFW.GLFW_KEY_UNKNOWN),
				debug = KeybindingHelper.getKeybinding(Data.version.getID(), Data.version.getID(), "debug", GLFW.GLFW_KEY_UNKNOWN),
				debug_2 = KeybindingHelper.getKeybinding(Data.version.getID(), Data.version.getID(), "debug_2", GLFW.GLFW_KEY_UNKNOWN)
		};
	}
	public static void init() {
		Data.version.sendToLog(LogType.INFO, Translation.getString("Initializing keybindings!"));
	}
	public static void tick() {
		if (openConfig.wasPressed()) {
			ClientData.minecraft.setScreen(new ConfigScreen(ClientData.minecraft.currentScreen, false, DateHelper.isPride()));
		}
		if (debug.wasPressed()) Debug.debugShader = !Debug.debugShader;
		if (debug_2.wasPressed()) {
			switch (Debug.debugRenderType) {
				case GAME -> Debug.debugRenderType = Shader.RenderType.WORLD;
				case WORLD -> Debug.debugRenderType = Shader.RenderType.GAME;
			}
		}
	}
}