/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.Runnables;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MessageOverlay {
	public static Text message;
	public static float remaining;
	public static void init() {
		Events.AfterInGameHudRender.register(Identifier.of(Data.getVersion().getID(), "message_overlay"), new Runnables.InGameHudRender() {
			public void run(DrawContext context, RenderTickCounter renderTickCounter) {
				int time = (int) Math.min((remaining - ClientData.minecraft.getRenderTickCounter().getTickDelta(true)) * 255.0F / 20.0F, 255.0F);
				if (time > 10) context.drawCenteredTextWithShadow(ClientData.minecraft.textRenderer, message, (int) (ClientData.minecraft.getWindow().getScaledWidth() / 2.0F), 23, 16777215 | (time << 24 & -16777216));
			}
		});
	}
	public static void tick() {
		if (remaining > 0) remaining -= 1;
	}
	public static void setOverlay(Text text) {
		message = text;
		remaining = 40;
	}
}