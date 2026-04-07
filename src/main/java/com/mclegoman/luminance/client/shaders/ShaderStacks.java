/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.Couple;
import com.mclegoman.luminance.common.util.DateHelper;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;

public class ShaderStacks {
    public static final Map<Identifier, Map<Identifier, Entry>> registries = new HashMap<>();

    protected static void init() {
        Events.AfterShaderDataRegistered.register(getMainRegistryId(), ShaderStacks::reload);
    }

    public static Identifier getMainRegistryId() {
        return Shaders.getMainRegistryId();
    }

    public static boolean shouldShowNamespace(Identifier registryId, Identifier shaderId) {
        List<String> shaderNames = new ArrayList<>();
        for (Identifier shader : getRegistry(registryId).keySet()) {
            if (shaderId.getPath().equals(shader.getPath())) shaderNames.add(shaderId.getPath());
        }
        return shaderNames.size() > 1;
    }

    public static Map<Identifier, Entry> getRegistry() {
        return getRegistry(getMainRegistryId());
    }

    public static Map<Identifier, Entry> getRegistry(Identifier registryId) {
        if (!registries.containsKey(registryId)) registries.put(registryId, new HashMap<>());
        return registries.get(registryId);
    }

    public static List<Identifier> getShaderStacks(Identifier registryId) {
        return getRegistry(registryId).keySet().stream().sorted().toList();
    }

    public static List<Identifier> getShaderStacks() {
        return getShaderStacks(getMainRegistryId());
    }

    public static void addStack(Identifier registryId, Identifier stackId, Entry.Text text, List<Entry.ShaderInfo> shaders, JsonObject customData) {
        Map<Identifier, Entry> registry = getRegistry(registryId);
        if (!registry.containsKey(stackId)) registry.put(stackId, new ShaderStacks.Entry(text, shaders, customData));
    }

    public static void addStack(Identifier stackId, Entry.Text text, List<Entry.ShaderInfo> shaders, JsonObject customData) {
        addStack(getMainRegistryId(), stackId, text, shaders, customData);
    }

    public static List<Identifier> getRegistries() {
        return registries.keySet().stream().toList();
    }

    public static void resetRegistries() {
        registries.clear();
    }

    private static void addDefaultStacks() {
        for (Identifier registryId : Shaders.getRegistries()) {
            for (ShaderRegistryEntry shader : Shaders.getRegistry(registryId)) {
                addStack(registryId, shader.getID(), new Entry.Text(shader.getID(), false), List.of(new Entry.ShaderInfo(registryId, shader.getID())), shader.getCustom());
            }
        }
    }

    public static Entry getStack(Identifier registryId, Identifier stackId) {
        return getRegistry(registryId).get(stackId);
    }

    public static Entry getStack(Identifier stackId) {
        return getRegistry(getMainRegistryId()).get(stackId);
    }

    public static Optional<JsonObject> getCustom(Identifier registryId, Identifier stackId) {
        Entry stack = getStack(registryId, stackId);
        return stack != null ? Optional.of(stack.customData()) : Optional.empty();
    }

    public static Optional<JsonObject> getCustom(Identifier registryId, Identifier stackId, String namespace) {
        Optional<JsonObject> customData = getCustom(registryId, stackId);
        return customData.isPresent() && customData.get().has(namespace) ? Optional.of(customData.get().get(namespace).getAsJsonObject()) : Optional.empty();
    }

    public static Identifier getShadersId(Identifier renderId, String string) {
        return renderId.withPath(renderId.getPath() + "_" + string);
    }

    public static Events.ShaderRenderData getShaders(Identifier renderId, Entry stack, Callable<RenderLocations.RenderLocation> renderLocation, Callable<Boolean> enabled, Callables.ShaderRegistryCaller disablePhotosensitive) {
        List<Shader.Data> shaders = new ArrayList<>();
        if (stack != null) {
            int index = 0;
            for (Entry.ShaderInfo shaderInfo : stack.shaders()) {
                try {
                    shaders.add(new Shader.Data(getShadersId(renderId, String.valueOf(index)), new Shader(Shaders.get(shaderInfo.shaderRegistryId(), shaderInfo.shaderId()), renderLocation, enabled)));
                    index++;
                } catch (Exception error) {
                    Data.getVersion().sendToLog(LogType.WARN, "Failed to add '{}::{}' shader to shader stack!", shaderInfo.shaderRegistryId(), shaderInfo.shaderId());
                }
            }
        }
        return new Events.ShaderRenderData(shaders, disablePhotosensitive);
    }

    public static Optional<Identifier> guessStackId(@NotNull String id) {
        return guessStackId(getMainRegistryId(), id);
    }

    public static Optional<Identifier> guessStackId(@NotNull Identifier registry, @NotNull String id) {
        // If the stack registry contains at least one stack with the name, the first detected instance will be used.
        id = id.toLowerCase(Locale.ROOT);

        if (id.contains(":")) {
            Identifier identifier = Identifier.tryParse(id);
            if (identifier == null) {
                return Optional.empty();
            }

            Entry entry = getStack(registry, identifier);
            if (entry != null) {
                return Optional.of(identifier);
            }

            id = identifier.getPath();
        }

        for (Identifier shaderId : getRegistry(registry).keySet()) {
            if (shaderId.getPath().equals(id)) {
                return Optional.of(shaderId);
            }
        }

        return Optional.empty();
    }

    private static void reload() {
        try {
            resetRegistries();
            ClientData.minecraft.getResourceManager().listResources("shader_stack", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
                String stackId = identifier.getPath().substring(identifier.getPath().lastIndexOf("/") + 1, identifier.getPath().lastIndexOf(".json"));
                try (InputStream stream = resource.open()) {
                    JsonObject reader = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

                    if (Shaders.preventRegister(reader, identifier.withPath(stackId), "shader stack")) return;

                    JsonArray defaultRegistryIds = new JsonArray();
                    defaultRegistryIds.add(getMainRegistryId().toString());

                    JsonArray registryIds = reader.has("registries") ? reader.getAsJsonArray("registries") : defaultRegistryIds;

                    List<ShaderStacks.Entry.ShaderInfo> shaders = new ArrayList<>();
                    if (reader.has("shaders")) {
                        for (JsonElement shaderElement : reader.getAsJsonArray("shaders")) {
                            if (shaderElement instanceof JsonObject shaderData) {
                                if (shaderData.has("shader")) {
                                    Identifier registryId = shaderData.has("registry") ? Identifier.parse(shaderData.getAsJsonPrimitive("registry").getAsString()) : getMainRegistryId();
                                    Identifier shaderId = Identifier.parse(shaderData.getAsJsonPrimitive("shader").getAsString());
                                    ShaderRegistryEntry shaderRegistryEntry = Shaders.get(registryId, shaderId);
                                    if (shaderRegistryEntry != null) {
                                        // TODO: Add uniform modifiers.
                                        shaders.add(new ShaderStacks.Entry.ShaderInfo(registryId, shaderId));
                                    }
                                    else Data.getVersion().sendToLog(LogType.WARN, "Failed to add shader info to '{}' stack as we couldn't find a shader with the id '{}::{}'!", identifier.withPath(stackId), registryId, shaderId);
                                } else {
                                    Data.getVersion().sendToLog(LogType.WARN, "Failed to add shader info to '{}' stack due to missing shader id!", identifier.withPath(stackId));
                                }
                            }
                        }
                    }

                    Identifier id = reader.has("identifier") ? Identifier.parse(reader.get("identifier").getAsString()) : identifier.withPath(stackId);
                    registryIds.forEach(registryId -> addStack(Identifier.parse(registryId.getAsString()), id, new Entry.Text(id, true), shaders, reader.has("custom") ? reader.getAsJsonObject("custom") : new JsonObject()));
                } catch (Exception error) {
                    Data.getVersion().sendToLog(LogType.ERROR, "Failed to load shader stack '{}': {}", identifier.withPath(stackId), error.getLocalizedMessage());
                }
            });
        } catch (Exception error) {
            Data.getVersion().sendToLog(LogType.ERROR, "Failed to reload shader stacks: {}", error);
        }
        addDefaultStacks();
        Events.AfterShaderStacksRegistered.registry.forEach((id, runnable) -> {
            try {
                runnable.run();
            } catch (Exception error) {
                Data.getVersion().sendToLog(LogType.ERROR, "Failed to execute AfterShaderStacksRegistered event with id: {}:{}:", id, error);
            }
        });
    }

    public static Component getShaderName(Identifier registryId, Identifier shaderId) {
        return getStack(registryId, shaderId).text.getName(shouldShowNamespace(registryId, shaderId));
    }

    public static Component getShaderDescription(Identifier registryId, Identifier shaderId) {
        return getStack(registryId, shaderId).text.getDescription(shouldShowNamespace(registryId, shaderId));
    }

    public record Entry(Identifier registryId, Text text, List<Entry.ShaderInfo> shaders, JsonObject customData) {
        public Entry(Text text, List<Entry.ShaderInfo> shaders, JsonObject customData) {
            this(ShaderStacks.getMainRegistryId(), text, shaders, customData);
        }

        public record Text(Identifier id, boolean isStack) {
            public Text(Identifier id) {
                this(id, true);
            }

            public MutableComponent getName(boolean showNamespace) {
                return Entry.getComponent(id(), isStack(), false, showNamespace);
            }

            public MutableComponent getDescription(boolean showNamespace) {
                return Entry.getComponent(id(), isStack(), true, showNamespace);
            }

            public String getTranslationKey(boolean description) {
                return Entry.getTranslationKey(this.id, description);
            }
        }

        public static String getTranslationKey(Identifier id, boolean description) {
            return "gui." + Data.getVersion().getID() + ".shader_stack." + id.getNamespace() + "." + id.getPath() + (description ? ".description" : "");
        }

        public static MutableComponent getComponent(Identifier id, boolean isStack, boolean description, boolean showNamespace) {
            if (isStack) return Component.translatableWithFallback(getTranslationKey(id, description), !description ? id.toString() : "");
            else return Translation.getShaderText(id, showNamespace, description);
        }

        public record ShaderInfo(Identifier shaderRegistryId, Identifier shaderId) {
            // TODO: Add uniform modifiers.
        }
    }

    public static Couple<Identifier, Entry> getRandom(Identifier registryId) {
        List<Identifier> identifiers = new ArrayList<>(getRegistry(registryId).keySet());
        Identifier identifier = identifiers.get(randomIndex(identifiers.size()));
        return new Couple<>(identifier, getRegistry(registryId).get(identifier));
    }

    public static int randomIndex(int size) {
        return Math.floorMod(DateHelper.getDate().getDayOfYear() + DateHelper.getDate().getYear(), size - 1);
    }
}
