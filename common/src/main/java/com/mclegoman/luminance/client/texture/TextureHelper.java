/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.texture;

import net.minecraft.util.Identifier;

public class TextureHelper {
	public static Identifier getTexture(Identifier texture, Identifier current) {
		return texture.getPath().equals("none") ? current : Identifier.of(texture.getNamespace(), texture.getPath().endsWith(".png") ? texture.getPath() : texture.getPath() + ".png");
	}
}