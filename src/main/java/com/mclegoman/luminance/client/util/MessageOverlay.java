/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MessageOverlay {
	public static Component message;
	public static float remaining;
	public static void init() {
		Events.AfterInGameHudRender.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "message_overlay"), (context, renderTickCounter) -> {
            int time = (int) Math.min((remaining - ClientData.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true)) * 255.0F / 20.0F, 255.0F);
            if (time > 10) context.drawCenteredString(ClientData.minecraft.font, message, (int) (ClientData.minecraft.getWindow().getGuiScaledWidth() / 2.0F), 23, 16777215 | (time << 24 & -16777216));
        });
	}
	public static void tick() {
		if (remaining > 0) remaining -= 1;
	}
	public static void setOverlay(Component text) {
		message = text;
		remaining = 40;
	}
}