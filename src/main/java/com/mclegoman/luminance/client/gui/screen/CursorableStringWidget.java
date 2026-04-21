/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class CursorableStringWidget extends StringWidget {
    private final boolean canHandleCursor;

    public CursorableStringWidget(Component component, Font font) {
        this(component, font, false);
    }

    public CursorableStringWidget(Component component, Font font, boolean canHandleCursor) {
        super(component, font);
        this.canHandleCursor = canHandleCursor;
    }

    public CursorableStringWidget(int i, int j, Component component, Font font) {
        this(i, j, component, font, false);
    }

    public CursorableStringWidget(int i, int j, Component component, Font font, boolean canHandleCursor) {
        super(i, j, component, font);
        this.canHandleCursor = canHandleCursor;
    }

    public CursorableStringWidget(int i, int j, int k, int l, Component component, Font font) {
        this(i, j, k, l, component, font, false);
    }

    public CursorableStringWidget(int i, int j, int k, int l, Component component, Font font, boolean canHandleCursor) {
        super(i, j, k, l, component, font);
        this.canHandleCursor = canHandleCursor;
    }

    @Override
    protected void handleCursor(@NonNull GuiGraphics guiGraphics) {
        if (this.canHandleCursor) super.handleCursor(guiGraphics);
    }
}
