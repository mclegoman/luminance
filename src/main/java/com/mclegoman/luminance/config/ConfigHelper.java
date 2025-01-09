/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.config;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.util.math.MathHelper;

public class ConfigHelper {
	protected static final int defaultConfigVersion = 2;
	protected static final int defaultAlphaLevel = 100;
	protected static final boolean defaultShowAlphaLevelOverlay = false;
	public static void init() {
		try {
			Config.init();
			if (fixConfig()) saveConfig(false);
		} catch (Exception error) {
			Data.version.sendToLog(LogType.WARN, Translation.getString("Failed to initialize config!: {}", error));
		}
	}
	public static void saveConfig(boolean shouldFix) {
		try {
			if (shouldFix) fixConfig();
			Config.save();
		} catch (Exception error) {
			Data.version.sendToLog(LogType.WARN, "Failed to save config!");
		}
	}
	public static boolean fixConfig() {
		boolean hasFixedConfig = false;
		if ((int) getConfig("alpha_level") < 0 || (int) getConfig("alpha_level") > 100) {
			Data.version.sendToLog(LogType.WARN, "Config: alpha_level was invalid and have been reset to prevent any unexpected issues. (" + getConfig("alpha_level") + ")");
			hasFixedConfig = setConfig("alpha_level", 100);
		}
		return hasFixedConfig;
	}
	public static boolean resetConfig() {
		boolean configChanged = false;
		try {
			configChanged = setConfig("alpha_level", defaultAlphaLevel);
			configChanged = setConfig("show_alpha_level_overlay", defaultShowAlphaLevelOverlay);
		} catch (Exception error) {
			Data.version.sendToLog(LogType.WARN, "Failed to reset config!");
		}
		return configChanged;
	}
	public static boolean setConfig(String id, Object value) {
		boolean configChanged = false;
		try {
			switch (id) {
				case "alpha_level" -> {
					Config.alphaLevel = MathHelper.clamp((int) value, 0, 100);
					configChanged = true;
				}
				case "show_alpha_level_overlay" -> {
					Config.showAlphaLevelOverlay = (boolean)value;
					configChanged = true;
				}
				case "config_version" -> {
					Config.configVersion = (int) value;
					configChanged = true;
				}
				default -> Data.version.sendToLog(LogType.WARN, Translation.getString("Failed to set {} config value!: Invalid Key", id));
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.WARN, Translation.getString("Failed to set {} config value!: {}", id, error));
		}
		return configChanged;
	}
	public static Object getConfig(String id) {
		switch (id) {
			case "alpha_level" -> {
				return MathHelper.clamp(Config.alphaLevel, 0, 100);
			}
			case "show_alpha_level_overlay" -> {
				return Config.showAlphaLevelOverlay;
			}
			case "config_version" -> {
				return Config.configVersion;
			}
			default -> {
				Data.version.sendToLog(LogType.WARN, Translation.getString("Failed to get {} config value!: Invalid Key", id));
				return new Object();
			}
		}
	}
}