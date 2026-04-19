/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.config;

import com.mclegoman.luminance.client.config.value.SpectatorPriorityModeValue;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.common.data.Data;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedName;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

import java.nio.file.Paths;

public class LuminanceConfig extends ReflectiveConfig {
	// For more info on Quilt Config, see https://wiki.quiltmc.org/en/configuration/getting-started.
	public static final LuminanceConfig config;

	@Comment("Sets the luminance_alpha dynamic uniform (int)0-100%, outputs 0.0F-1.0F.")
	@IntegerRange(min = 0, max = 100)
	@SerializedName("alpha_level")
	public final TrackedValue<Integer> alphaLevel = this.value(100);

	@Comment("Sets whether the % is shown in MessageOverlay when adjusting alpha using the keybinding.")
	@SerializedName("show_alpha_level_overlay")
	public final TrackedValue<Boolean> showAlphaLevelOverlay = this.value(false);

	@Comment("")
	@SerializedName("spectator_priority_mode")
	public final TrackedValue<SpectatorPriorityModeValue> spectatorPriorityMode = this.value(SpectatorPriorityModeValue.of(SpectatorHandler.Mode.FIRST));

	@Comment("Forces Luminance into thinking it's in the development environment.")
	@SerializedName("debug")
	public final TrackedValue<Boolean> debug = this.value(false);

	public static void init() {
	}

	static {
		config = LuminanceConfig.createToml(Paths.get("config"), Data.getVersion().getID(), "config", LuminanceConfig.class);
	}
}