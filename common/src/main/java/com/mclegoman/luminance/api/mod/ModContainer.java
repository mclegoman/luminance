/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.api.mod;

import java.util.Collection;

public record ModContainer(ModMetadata metadata) {
	public record ModMetadata(String id, String rawVersion, String name, String description, Collection<String> licenses, Collection<String> contributors) {
	}
}
