/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.util.Identifier;

public class TextureHelper {
	public static void init() {
		try {
			ResourcePacks.init();
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to init texture helper: {}", error));
		}
	}
	public static Identifier getTexture(Identifier texture, Identifier current) {
		return texture.getPath().equals("none") ? current : Identifier.of(texture.getNamespace(), texture.getPath().endsWith(".png") ? texture.getPath() : texture.getPath() + ".png");
	}
}