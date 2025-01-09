/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.common.data.Data;

// The reason why we have our own Identifier, is because Minecraft's can't use capitals.

public record LuminanceIdentifier(String namespace, String path) {
	public LuminanceIdentifier(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
	}
	public static LuminanceIdentifier of(String id) {
		return splitOn(id, ':');
	}
	public static LuminanceIdentifier splitOn(String id, char delimiter) {
		int index = id.indexOf(delimiter);
		if (index >= 0) {
			String path = id.substring(index + 1);
			if (index != 0) {
				String namespace = id.substring(0, index);
				return of(namespace, path);
			} else {
				return ofLuminance(path);
			}
		} else {
			return ofLuminance(id);
		}
	}
	public static LuminanceIdentifier ofLuminance(String path) {
		return of(Data.version.getID(), path);
	}
	public static LuminanceIdentifier of(String namespace, String path) {
		return new LuminanceIdentifier(namespace, path);
	}
	public String toUnderscoreSeparatedString() {
		return this.toString().replace('/', '_').replace(':', '_');
	}
	public String toString() {
		return this.namespace + ":" + this.path;
	}
}
