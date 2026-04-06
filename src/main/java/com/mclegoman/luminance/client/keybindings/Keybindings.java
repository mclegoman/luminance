/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.keybindings;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.gui.screen.config.ConfigScreen;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.MessageOverlay;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.DateHelper;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Keybindings {
	public static final KeyMapping adjustAlpha;
	public static final KeyMapping openConfig;
	public static KeyMapping toggle_debug_shader;
	public static KeyMapping cycle_debug_render_type;
	public static final List<KeyMapping> allKeybindings = new ArrayList<>();
	static {
		allKeybindings.add(adjustAlpha = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "adjust_alpha", GLFW.GLFW_KEY_J));
		allKeybindings.add(openConfig = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "open_config", GLFW.GLFW_KEY_UNKNOWN));
		if (ClientData.isDevelopment()) {
			allKeybindings.add(toggle_debug_shader = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "toggle_debug_shader", GLFW.GLFW_KEY_UNKNOWN));
			allKeybindings.add(cycle_debug_render_type = KeybindingHelper.getKeybinding(Data.getVersion().getID(), Data.getVersion().getID(), "cycle_debug_render_type", GLFW.GLFW_KEY_UNKNOWN));
		} else {
			toggle_debug_shader = null;
			cycle_debug_render_type = null;
		}
	}
	public static void init() {
		Data.getVersion().sendToLog(LogType.INFO, "Initializing keybindings!");
	}
	public static void tick() {
		if (openConfig.consumeClick()) {
			ClientData.minecraft.setScreen(new ConfigScreen(ClientData.minecraft.screen, 0, null, DateHelper.isPride()));
		}
		if (ClientData.isDevelopment()) {
			if (toggle_debug_shader != null && toggle_debug_shader.consumeClick()) {
				Debug.setDebugShaderEnabled(!Debug.isDebugShaderEnabled());
				MessageOverlay.setOverlay(Translation.getTranslation(Data.getVersion().getID(), "debug.render", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", Debug.isDebugShaderEnabled())}));
			}
			if (cycle_debug_render_type != null && cycle_debug_render_type.consumeClick()) {
				Debug.cycleDebugRenderType(ClientData.minecraft.hasShiftDown()).ifPresent(renderType -> MessageOverlay.setOverlay(Translation.getTranslation(Data.getVersion().getID(), "debug.render_type", new Object[]{Component.translatableWithFallback("gui." + renderType.identifier().getNamespace() + ".render_type." + renderType.identifier().getPath(), renderType.toString())})));
			}
		}
	}
}