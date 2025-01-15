/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.util;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Version implements Comparable<Version> {
	private final String name;
	private final String id;
	private final int major;
	private final int minor;
	private final int patch;
	private final ReleaseType type;
	private final int build;
	private final boolean dirty;
	private final boolean hasModrinthId;
	private final String modrinthId;
	private Version(String name, String id, int major, int minor, int patch, ReleaseType type, int build, boolean dirty, boolean hasModrinthId, String modrinthId) {
		this.name = name;
		this.id = id;
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.type = type;
		this.build = build;
		this.dirty = dirty;
		this.hasModrinthId = hasModrinthId;
		this.modrinthId = modrinthId;
	}
	// dirty should be set to false for versions released to modrinth (etc). dirty should only be set to true if you are building a version that won't get released.
	public static Version create(String name, String id, int major, int minor, int patch, ReleaseType type, int build, boolean dirty, String modrinthId) {
		return new Version(name, id, major, minor, patch, type, build, dirty, true, modrinthId);
	}
	public static Version create(String name, String id, int major, int minor, int patch, ReleaseType type, boolean dirty, int build) {
		return new Version(name, id, major, minor, patch, type, build, dirty, false, "");
	}
	public static Version create(String name, String id, int major, int minor, int patch, ReleaseType type, int build, String modrinthId) {
		return new Version(name, id, major, minor, patch, type, build, false, true, modrinthId);
	}
	public static Version create(String name, String id, int major, int minor, int patch, ReleaseType type, int build) {
		return new Version(name, id, major, minor, patch, type, build, false, false, "");
	}
	public static Version parse(ModMetadata metadata) {
		return parse(metadata, "");
	}
	public static Version parse(ModMetadata metadata, String modrinthId) {
		String version = metadata.getVersion().getFriendlyString();
		String[] versionData = version.split("-");
		String[] versionVer = versionData[0].split("\\.");
		String[] versionType = versionData[1].split("\\.");
		String[] versionTypeVer = (versionType[1].replace(".", "")).split("\\+");
		return create(metadata.getName(),
				metadata.getId(),
				Integer.parseInt(versionVer[0].replace(".", "")),
				Integer.parseInt(versionVer[1].replace(".", "")),
				Integer.parseInt(versionVer[2].replace(".", "")),
				Helper.stringToType(versionType[0].replace("-", "")),
				Integer.parseInt(versionTypeVer[0].replace(".", "").replace("+", "")),
				(versionTypeVer.length >= 2 && versionTypeVer[1].replace(".", "").replace("+", "").equalsIgnoreCase("dirty")),
				modrinthId);
	}
	public String getFriendlyString(boolean full) {
		return full ? getFriendlyString() : (getType().equals(ReleaseType.RELEASE) ? String.format("%s.%s.%s", getMajor(), getMinor(), getPatch()) : getFriendlyString());
	}
	public boolean hasModrinthID() {
		return this.hasModrinthId;
	}
	public String getModrinthID() {
		return this.modrinthId;
	}
	public Optional<ModContainer> getModContainer() {
		return Data.getModContainer(getID());
	}
	public String getName() {
		return name;
	}
	public String getID() {
		return id;
	}
	public int getMajor() {
		return major;
	}
	public int getMinor() {
		return minor;
	}
	public int getPatch() {
		return patch;
	}
	public ReleaseType getType() {
		return type;
	}
	public int getBuild() {
		return build;
	}
	public boolean getDirty() {
		return dirty;
	}
	public String getFriendlyString() {
		return Translation.getString("{}.{}.{}-{}.{}{}", getMajor(), getMinor(), getPatch(), Helper.releaseTypeString(getType(), Helper.TranslationType.CODE), getBuild(), (getDirty() ? "+dirty" : ""));
	}
	public String getLoggerPrefix() {
		return Translation.getString("[{} {}]", getName(), getFriendlyString());
	}
	public boolean isDevelopmentBuild() {
		return !type.equals(ReleaseType.RELEASE);
	}
	private Logger getLogger() {
		return LoggerFactory.getLogger(getName());
	}
	@Override
	public int compareTo(Version other) {
		if (major != other.major) {
			return Integer.compare(major, other.major);
		} else if (minor != other.minor) {
			return Integer.compare(minor, other.minor);
		} else if (patch != other.patch) {
			return Integer.compare(patch, other.patch);
		} else if (type != other.type) {
			return type.compareTo(other.type);
		} else {
			return Integer.compare(build, other.build);
		}
	}
	public void sendToLog(LogType logType, String logMessage) {
		if (logType.equals(LogType.INFO)) getLogger().info(Translation.getString("{} {}", getLoggerPrefix(), logMessage));
		if (logType.equals(LogType.WARN)) getLogger().warn(Translation.getString("{} {}", getLoggerPrefix(), logMessage));
		if (logType.equals(LogType.ERROR)) getLogger().error(Translation.getString("{} {}", getLoggerPrefix(), logMessage));
		if (logType.equals(LogType.DEBUG)) getLogger().debug(Translation.getString("{} {}", getLoggerPrefix(), logMessage));
	}
}