/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.metadata.ContactInformationImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

import java.net.URI;
import java.util.*;

public class AttributionsWidget {
    private static List<FormattedText> getTexts() {
        List<FormattedText> texts = new ArrayList<>();
        List<FormattedText> developers = new ArrayList<>();
        List<FormattedText> contributors = new ArrayList<>();
        Data.getVersion().getModContainer().ifPresent(modContainer -> {
            texts.add(getDescription(modContainer.getMetadata()));
            getLicense(modContainer.getMetadata()).ifPresent(texts::add);
            texts.add(empty());

            for (Person developer : modContainer.getMetadata().getAuthors()) developers.add(getPersonName(developer));
            for (Person contributor : modContainer.getMetadata().getContributors()) contributors.add(getPersonName(contributor));
        });

        if (!developers.isEmpty()) {
            texts.add(Translation.getTranslation(Data.getVersion().getID(), "developers", new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}));
            texts.addAll(developers);

            texts.add(empty());
        }

        if (!contributors.isEmpty()) {
            texts.add(Translation.getTranslation(Data.getVersion().getID(), "contributors", new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}));
            texts.addAll(contributors);

            texts.add(empty());
        }

        texts.add(Translation.getTranslation(Data.getVersion().getID(), "attributions", new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}));

        texts.addAll(getAttribution(createMetadata("Quilt Config", "A library designed to facilitate the creation and management of config files.", List.of("QuiltMC"), List.of("Apache-2.0"), "https://github.com/QuiltMC/quilt-config")));

        texts.add(empty());

        Data.getVersion().getModContainer().ifPresent(modContainer -> {
            for (ModDependency dependency : modContainer.getMetadata().getDependencies()) {
                FabricLoader.getInstance().getModContainer(dependency.getModId()).ifPresent(fabric -> {
                    if (!fabric.getMetadata().getId().equalsIgnoreCase("minecraft")) {
                        texts.addAll(getAttribution(fabric.getMetadata()));
                        texts.add(empty());
                    }
                });
            }
        });

        texts.addAll(getAttribution(createMetadata("Minecraft", "The base game.", List.of("Mojang Studios"), List.of("Minecraft EULA"), "https://minecraft.net")));

        return texts;
    }

    public static ModMetadata createMetadata(String name, String description, List<String> authors, List<String> licenses, String homepage) {
        BuiltinModMetadata.Builder builder = new BuiltinModMetadata.Builder(name.toLowerCase(), "0").setName(name).setDescription(description);
        for (String author : authors) builder.addAuthor(author, Map.of());
        for (String license : licenses) builder.addLicense(license);
        if (homepage != null && !homepage.isBlank()) builder.setContact(new ContactInformationImpl(Map.of("homepage", homepage)));
        return builder.build();
    }

    public static ScrollableTextWidget get(Minecraft minecraft, int width, int height, int y, int lineHeight, double scrollY) {
        return new ScrollableTextWidget(minecraft, width, height, y, lineHeight, scrollY, getTexts());
    }

    public static ScrollableTextWidget get(Minecraft minecraft, int width, int height, int y, int lineHeight) {
        return new ScrollableTextWidget(minecraft, width, height, y, lineHeight, getTexts());
    }

    private static FormattedText getPersonName(Person person) {
        return getLiteral(person.getName(), person.getContact().get("homepage").orElse(null));
    }

    private static FormattedText getDescription(ModMetadata modMetadata) {
        return getLiteral(modMetadata.getDescription(), null);
    }

    private static Collection<String> authorsAsString(Collection<Person> people) {
        Collection<String> authors = new ArrayList<>();
        for (Person person : people) authors.add(person.getName());
        return authors;
    }

    private static Optional<FormattedText> getAuthor(ModMetadata modMetadata) {
        Collection<String> authors = authorsAsString(modMetadata.getAuthors());
        return !authors.isEmpty() ? Optional.of(Translation.getTranslation(Data.getVersion().getID(), authors.size() > 1 ? "authors.multiple" : "authors.single", new Object[]{String.join(",", authors)}, new ChatFormatting[]{ChatFormatting.GRAY})) : Optional.empty();
    }

    private static Optional<FormattedText> getLicense(ModMetadata modMetadata) {
        Collection<String> license = modMetadata.getLicense();
        return !license.isEmpty() ? Optional.of(Translation.getTranslation(Data.getVersion().getID(), license.size() > 1 ? "license.multiple" : "license.single", new Object[]{String.join(",", license)}, new ChatFormatting[]{ChatFormatting.GRAY})) : Optional.empty();
    }

    private static List<FormattedText> getAttribution(ModMetadata modMetadata) {
        List<FormattedText> texts = new ArrayList<>();
        texts.add(getAttributionName(modMetadata, ChatFormatting.WHITE));
        texts.add(getDescription(modMetadata));
        getAuthor(modMetadata).ifPresent(texts::add);
        getLicense(modMetadata).ifPresent(texts::add);
        return texts;
    }

    private static FormattedText getAttributionName(ModMetadata modMetadata) {
        return getAttributionName(modMetadata, ChatFormatting.GRAY);
    }

    private static FormattedText getAttributionName(ModMetadata modMetadata, ChatFormatting... chatFormattings) {
        return getLiteral(modMetadata.getName(), modMetadata.getContact().get("homepage").orElse(null), chatFormattings);
    }

    private static FormattedText getLiteral(String key, String homepage) {
        return getLiteral(key, homepage, ChatFormatting.GRAY);
    }

    private static FormattedText getLiteral(String key, String homepage, ChatFormatting... chatFormattings) {
        return getText(key, false, homepage, chatFormattings);
    }

    private static FormattedText getText(String key, boolean translatable, String homepage, ChatFormatting[] chatFormattings, Object... args) {
        MutableComponent text = translatable ? Component.translatable(key, args) : Component.literal(key);
        if (chatFormattings != null) text.withStyle(chatFormattings);
        if (homepage != null && !homepage.isBlank()) text.setStyle(text.getStyle().withClickEvent(new ClickEvent.OpenUrl(URI.create(homepage))));
        return text;
    }
    
    private static FormattedText empty() {
        return Component.literal(" ");
    }
}
