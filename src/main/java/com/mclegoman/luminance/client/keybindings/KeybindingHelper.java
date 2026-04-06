/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.keybindings;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.translation.Translation;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class KeybindingHelper {
	public static boolean hasKeybindingConflicts(KeyMapping... keybindings) {
		for (KeyMapping currentKey1 : keybindings) {
			for (KeyMapping currentKey2 : ClientData.minecraft.options.keyMappings) {
				if (!currentKey1.isUnbound() && !currentKey2.isUnbound()) {
					if (currentKey1 != currentKey2) {
						if (KeyBindingHelper.getBoundKeyOf(currentKey1) == KeyBindingHelper.getBoundKeyOf(currentKey2))
							return true;
					}
				}
			}
		}
		return false;
	}

	private static final HashMap<Identifier, KeyMapping.Category> createdCategories = new HashMap<>();

	public static KeyMapping getKeybinding(String namespace, String category, String key, int keyCode) {
		// This creates a category with the label "key.category.namespace.path".
		// KeyMapping.Category is a record, so we can't extend it to modify the label().
		// If we could, we could easily register a custom category using an accessor to get the sort order.
		// Ideally we'd like to change the label to "gui.namespace.keybindings.category.path"
		return KeyBindingHelper.registerKeyBinding(new KeyMapping(Translation.getKeybindingTranslation(namespace, key), InputConstants.Type.KEYSYM, keyCode, createdCategories.computeIfAbsent(Identifier.fromNamespaceAndPath(namespace, category), KeyMapping.Category::register)));
	}
}