/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.config;

import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.config.LuminanceConfigHelper;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;

public class LuminanceConfig extends ReflectiveConfig {
	public static final LuminanceConfig config = LuminanceConfigHelper.register(LuminanceConfigHelper.SerializerType.TOML, Data.version.getID(), "client", LuminanceConfig.class);
	@Comment("Sets the luminance_alpha dynamic uniform (int)0-100%, outputs 0.0F-1.0F.")
	public final TrackedValue<Integer> alphaLevel = this.value(100);
	@Comment("Sets whether the % is shown in MessageOverlay when adjusting alpha using the keybinding.")
	public final TrackedValue<Boolean> showAlphaLevelOverlay = this.value(false);
}