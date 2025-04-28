/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.keybindings;

import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.option.KeyBinding;

public class KeybindingHelper {
	public static boolean hasKeybindingConflicts(KeyBinding... keybindings) {
		return false;
	}
	public static KeyBinding getKeybinding(String namespace, String category, String key, int keyCode) {
		// This should get overridden by the mod loader.
		Data.getVersion().sendToLog(LogType.WARN, "Attempted to register keybinding using :common instead of specific mod-loader! This is a known issue that only effects the dev env, refreshing gradle can sometimes fix it.");
		return null;
	}
}