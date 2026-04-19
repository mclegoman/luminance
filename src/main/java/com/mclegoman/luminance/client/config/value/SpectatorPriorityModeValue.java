/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.config.value;

import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;

@SuppressWarnings("unused")
public record SpectatorPriorityModeValue(SpectatorHandler.Mode mode) implements ConfigSerializableObject<String> {
	public SpectatorHandler.Mode getMode() {
		return this.mode;
	}
	public static SpectatorPriorityModeValue of(SpectatorHandler.Mode mode) {
		return new SpectatorPriorityModeValue(mode);
	}
	public SpectatorPriorityModeValue convertFrom(String representation) {
		SpectatorHandler.Mode modeFromRepresentation = SpectatorHandler.Mode.FIRST;
		// If the value doesn't exist, we just use Mode.FIRST instead.
		try {
			modeFromRepresentation = SpectatorHandler.Mode.valueOf(representation);
		} catch (IllegalArgumentException error) {
			Data.getVersion().sendToLog(LogType.WARN, "'" + representation + "' was not a valid SpectatorHandler.Mode, using 'SpectatorHandler.Mode.FIRST' instead: " + error.getLocalizedMessage());
		}
		return of(modeFromRepresentation);
	}
	public String getRepresentation() {
		return this.mode.toString();
	}
	public SpectatorPriorityModeValue copy() {
		return this;
	}
}