/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.TimeZone;

public class DateHelper {
	// We use the GMT+12 timezone to make sure all players have the same experience.
	public static LocalDate getDate() {
		return LocalDate.now(TimeZone.getTimeZone("GMT+12").toZoneId());
	}
	public static LocalTime getTime() {
		return LocalTime.now(TimeZone.getTimeZone("GMT+12").toZoneId());
	}
	public static boolean isAprilFools() {
		LocalDate date = getDate();
		return (date.getMonth() == Month.APRIL && date.getDayOfMonth() <= 2);
	}
	public static boolean isPride() {
		LocalDate date = getDate();
		return date.getMonth() == Month.JUNE || date.getMonth() == Month.JULY && date.getDayOfMonth() <= 2;
	}
}
