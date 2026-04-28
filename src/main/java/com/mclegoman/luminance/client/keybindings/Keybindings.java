/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.keybindings;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.gui.screen.config.ConfigScreen;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.util.DateHelper;
import dev.dannytaylor.perspective.seam.client.events.SeamClientEvents;
import dev.dannytaylor.perspective.seam.common.data.FabricMod;
import dev.dannytaylor.perspective.seam.common.events.SeamEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Keybindings {
	public static final KeyMapping adjustAlpha;
	public static final KeyMapping openConfig;
	public static KeyMapping toggle_debug_shader;
	public static KeyMapping cycle_debug_render_location;
	public static final List<KeyMapping> allKeybindings = new ArrayList<>();

	static {
		allKeybindings.add(adjustAlpha = KeybindingHelper.getKeybinding(LuminanceClient.getMod().getId(), LuminanceClient.getMod().getId(), "adjust_alpha", GLFW.GLFW_KEY_J));
		allKeybindings.add(openConfig = KeybindingHelper.getKeybinding(LuminanceClient.getMod().getId(), LuminanceClient.getMod().getId(), "open_config", GLFW.GLFW_KEY_UNKNOWN));
		if (ClientData.isDevelopment()) {
			allKeybindings.add(toggle_debug_shader = KeybindingHelper.getKeybinding(LuminanceClient.getMod().getId(), LuminanceClient.getMod().getId(), "toggle_debug_shader", GLFW.GLFW_KEY_UNKNOWN));
			allKeybindings.add(cycle_debug_render_location = KeybindingHelper.getKeybinding(LuminanceClient.getMod().getId(), LuminanceClient.getMod().getId(), "cycle_debug_render_location", GLFW.GLFW_KEY_UNKNOWN));
		} else {
			toggle_debug_shader = null;
			cycle_debug_render_location = null;
		}
	}

	public static void onInitialize(FabricMod mod) {
		SeamEvents.onInitialize(mod, "Keybindings", () -> {});
	}

	public static void onTick() {
		if (openConfig.consumeClick()) {
			ClientData.minecraft.setScreen(new ConfigScreen(ClientData.minecraft.screen, 0, null, DateHelper.isPride()));
		}
		if (ClientData.isDevelopment()) {
			if (toggle_debug_shader != null && toggle_debug_shader.consumeClick()) {
				Debug.setDebugShaderEnabled(!Debug.isDebugShaderEnabled());
				SeamClientEvents.sendToMessageBar(Translation.getTranslation(LuminanceClient.getMod().getId(), "debug.render", new Object[]{Translation.getVariableTranslation(LuminanceClient.getMod().getId(), "onff", Debug.isDebugShaderEnabled())}));
			}
			if (cycle_debug_render_location != null && cycle_debug_render_location.consumeClick()) {
				Debug.cycleDebugRenderLocation(ClientData.minecraft.hasShiftDown()).ifPresent(renderLocation -> SeamClientEvents.sendToMessageBar(Translation.getTranslation(LuminanceClient.getMod().getId(), "debug.render_location", new Object[]{Component.translatableWithFallback("gui." + renderLocation.identifier().getNamespace() + ".render_location." + renderLocation.identifier().getPath(), renderLocation.toString())})));
			}
		}
	}
}