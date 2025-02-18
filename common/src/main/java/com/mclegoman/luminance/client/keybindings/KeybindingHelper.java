/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.keybindings;

import net.minecraft.client.option.KeyBinding;

public class KeybindingHelper {
	public static boolean hasKeybindingConflicts(KeyBinding... keybindings) {
		return false;
	}
	public static KeyBinding getKeybinding(String namespace, String category, String key, int keyCode) {
		// This should get overridden by the mod loader.
		return null;
	}
}