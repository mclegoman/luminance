/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.config;

import com.mclegoman.luminance.config.LuminanceConfigHelper;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.IntegerRange;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.TrackedValue;

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
	@Comment("Forces Luminance into thinking it's in the development environment.")
	@SerializedName("debug")
	public final TrackedValue<Boolean> debug = this.value(false);
	public static void init() {
	}
	static {
		config = LuminanceConfigHelper.register(LuminanceConfigHelper.SerializerType.PROPERTIES, "", "luminance", LuminanceConfig.class);
	}
}