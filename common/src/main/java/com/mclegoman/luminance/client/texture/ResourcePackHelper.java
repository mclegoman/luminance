/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import com.mclegoman.luminance.common.util.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ResourcePackHelper {
	public static void register(Identifier id, ModContainer container, Text text, ActivationType activationType) {
		// This should be overriden by the modloader subprojects.
	}
}