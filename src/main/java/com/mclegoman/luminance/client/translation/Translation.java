/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.translation;

import com.mclegoman.luminance.client.shaders.RenderLocations;
import com.mclegoman.luminance.mixin.client.gui.StringSplitterAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Translation {
	public static MutableComponent getText(String string, boolean isTranslatable) {
		return getText(string, isTranslatable, new Object[]{});
	}

	public static MutableComponent getText(String string, boolean isTranslatable, ChatFormatting[] formattings) {
		return getText(string, isTranslatable).withStyle(formattings);
	}

	public static MutableComponent getText(String string, boolean isTranslatable, Object[] variables) {
		return isTranslatable ? Component.translatable(string, variables) : Component.literal(getString(string, variables));
	}

	public static MutableComponent getText(String string, boolean isTranslatable, Object[] variables, ChatFormatting[] formattings) {
		return getText(string, isTranslatable, variables).withStyle(formattings);
	}

	public static MutableComponent getText(Data data) {
		return getText(data.key(), data.translatable());
	}

	public static MutableComponent getText(Data data, ChatFormatting[] formattings) {
		return getText(data.key(), data.translatable(), formattings);
	}

	public static MutableComponent getText(Data data, Object[] variables) {
		return getText(data.key(), data.translatable(), variables);
	}

	public static MutableComponent getText(Data data, Object[] variables, ChatFormatting[] formattings) {
		return getText(data.key(), data.translatable(), variables, formattings);
	}

	public static MutableComponent getCombinedText(MutableComponent... texts) {
		MutableComponent outputText = getText("", false);
		for (Component text : texts) outputText.append(text);
		return outputText;
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, Object[] variables, ChatFormatting[] formattings, boolean hover) {
		return hover ? getTranslation(namespace, "config." + name + ".hover", variables, formattings) : getTranslation(namespace, "config." + name, variables, formattings);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, Object[] variables, ChatFormatting[] formattings) {
		return getTranslation(namespace, "config." + name, variables, formattings);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, Object[] variables, boolean hover) {
		return hover ? getTranslation(namespace, "config." + name + ".hover", variables) : getTranslation(namespace, "config." + name, variables);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, Object[] variables) {
		return getTranslation(namespace, "config." + name, variables);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, ChatFormatting[] formattings, boolean hover) {
		return hover ? getTranslation(namespace, "config." + name + ".hover", formattings) : getTranslation(namespace, "config." + name, formattings);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, ChatFormatting[] formattings) {
		return getTranslation(namespace, "config." + name, formattings);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name, boolean hover) {
		return hover ? getTranslation(namespace, "config." + name + ".hover") : getTranslation(namespace, "config." + name);
	}

	public static MutableComponent getConfigTranslation(String namespace, String name) {
		return getTranslation(namespace, "config." + name);
	}

	public static MutableComponent getRenderLocationTranslation(Identifier renderLocationId) {
		return Component.translatableWithFallback("gui." + renderLocationId.getNamespace() + ".render_location." + renderLocationId.getPath(), renderLocationId.toString());
	}

	public static MutableComponent getRenderLocationTranslation(RenderLocations.RenderLocation renderLocation) {
		return getRenderLocationTranslation(renderLocation.identifier());
	}

	public static MutableComponent getTranslation(String namespace, String key, Object[] variables, ChatFormatting[] formattings) {
		return getText("gui." + namespace + "." + key, true, variables, formattings);
	}

	public static MutableComponent getTranslation(String namespace, String key, Object[] variables) {
		return getText("gui." + namespace + "." + key, true, variables);
	}

	public static MutableComponent getTranslation(String namespace, String key, ChatFormatting[] formattings) {
		return getText("gui." + namespace + "." + key, true, formattings);
	}

	public static MutableComponent getTranslation(String namespace, String key) {
		return getText("gui." + namespace + "." + key, true);
	}

	public static String getFormattedString(String value, String searchString, Object[] variables) {
		String string = value;
		for (Object variable : variables) string = StringUtils.replaceOnce(string, searchString, String.valueOf(variable));
		return string;
	}

	public static String getString(String string, Object... variables) {
		return getFormattedString(string, "{}", variables);
	}

	public static String getKeybindingTranslation(String namespace, String key, boolean category) {
		return category ? getString("gui.{}.keybindings.category.{}", namespace, key) : getString("gui.{}.keybindings.keybinding.{}", namespace, key);
	}

	public static String getKeybindingTranslation(String namespace, String key) {
		return getString("gui.{}.keybindings.keybinding.{}", namespace, key);
	}

	public static MutableComponent getVariableTranslation(String namespace, String type, boolean toggle) {
		return toggle ? getTranslation(namespace, "variable." + type + ".true") : getTranslation(namespace, "variable." + type + ".false");
	}

	public static MutableComponent getErrorTranslation(String namespace) {
		return getConfigTranslation(namespace, "error", new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD});
	}

	public static MutableComponent getTranslation(String type, String namespace, String key, Object[] variables, ChatFormatting[] formattings) {
		return getText(type + "." + namespace + "." + key, true, variables, formattings);
	}

	public static MutableComponent getTranslation(String type, String namespace, String key, Object[] variables) {
		return getText(type + "." + namespace + "." + key, true, variables);
	}

	public static MutableComponent getTranslation(String type, String namespace, String key, ChatFormatting[] formattings) {
		return getText(type + "." + namespace + "." + key, true, formattings);
	}

	public static MutableComponent getTranslation(String type, String namespace, String key) {
		return getText(type + "." + namespace + "." + key, true);
	}

	public static MutableComponent getItemTranslation(String namespace, String key, Object[] variables, ChatFormatting[] formattings) {
		return getTranslation("item", namespace, key, variables, formattings);
	}

	public static MutableComponent getItemTranslation(String namespace, String key, Object[] variables) {
		return getTranslation("item", namespace, key, variables);
	}

	public static MutableComponent getItemTranslation(String namespace, String key, ChatFormatting[] formattings) {
		return getTranslation("item", namespace, key, formattings);
	}

	public static MutableComponent getItemTranslation(String namespace, String key) {
		return getTranslation("item", namespace, key);
	}

	public static MutableComponent getShaderText(Identifier shaderId, boolean shouldShowNamespace, boolean description, ChatFormatting[] formattings) {
		MutableComponent text = Component.translatableWithFallback(getString("gui.{}.shader.{}.{}{}", com.mclegoman.luminance.common.data.Data.getVersion().getID(), shaderId.getNamespace(), shaderId.getPath(), (description ? ".description" : "")), description ? "" : getString((shouldShowNamespace ? shaderId.getNamespace() : "") + shaderId.getPath()));
		if (formattings != null) text.withStyle(formattings);
		return text;
	}

	public static MutableComponent getShaderText(Identifier shaderId, boolean shouldShowNamespace, ChatFormatting[] formattings) {
		return getShaderText(shaderId, shouldShowNamespace, false, formattings);
	}

	public static MutableComponent getShaderText(Identifier shaderId, boolean shouldShowNamespace, boolean description) {
		return getShaderText(shaderId, shouldShowNamespace, description, null);
	}

	public static MutableComponent getShaderText(Identifier shaderId, boolean shouldShowNamespace) {
		return getShaderText(shaderId, shouldShowNamespace, null);
	}

	public static Data data(String key, boolean translatable) {
		return new Data(key, translatable);
	}

	public record Data(String key, boolean translatable) {
	}

	@Nullable
	public static Style getStyleAt(FormattedText text, int x, StringSplitter textHandler) {
		WidthLimitingVisitor widthLimitingVisitor = new WidthLimitingVisitor((float)x, textHandler);
		return text.visit((style, string) -> StringDecomposer.iterateFormatted(string, style, widthLimitingVisitor) ? Optional.empty() : Optional.of(style), Style.EMPTY).orElse(null);
	}

	@Nullable
	public static Style getStyleAt(FormattedCharSequence text, int x, StringSplitter textHandler) {
		WidthLimitingVisitor widthLimitingVisitor = new WidthLimitingVisitor((float)x, textHandler);
		MutableObject<Style> mutableObject = new MutableObject<>();
		text.accept((index, style, codePoint) -> {
			if (!widthLimitingVisitor.accept(index, style, codePoint)) {
				mutableObject.setValue(style);
				return false;
			} else {
				return true;
			}
		});
		return mutableObject.get();
	}

	@Environment(EnvType.CLIENT)
	static class WidthLimitingVisitor implements FormattedCharSink {
		private float widthLeft;
		private int length;
		private final StringSplitter textHandler;

		public WidthLimitingVisitor(float maxWidth, StringSplitter textHandler) {
			this.widthLeft = maxWidth;
			this.textHandler = textHandler;
		}

		public boolean accept(int i, Style style, int j) {
			this.widthLeft -= ((StringSplitterAccessor)this.textHandler).getWidthProvider().getWidth(j, style);
			if (this.widthLeft >= 0.0F) {
				this.length = i + Character.charCount(j);
				return true;
			} else {
				return false;
			}
		}

		public int getLength() {
			return this.length;
		}

		public void resetLength() {
			this.length = 0;
		}
	}
}