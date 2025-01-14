package com.mclegoman.luminance.client.shaders.uniforms;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniformConfig {
    public static final UniformConfig EMPTY = new UniformConfig(new ArrayList<>());

    public final Map<String, List<Object>> config;

    public UniformConfig(List<ConfigData> configValues) {
        this.config = new HashMap<>(configValues.size());
        for (ConfigData configData : configValues) {
            this.config.put(configData.name, configData.objects);
        }
    }

    public Number getOrDefault(String name, int index, Number defaultValue) {
        List<Object> objects = config.get(name);
        if (objects == null || objects.size() <= index) {
            return defaultValue;
        }

        Object object = objects.get(index);
        if (object instanceof Number number) {
            return number;
        }

        return defaultValue;
    }

    public record ConfigData(String name, List<Object> objects) {
        public static final Codec<ConfigData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.STRING.fieldOf("name").forGetter(ConfigData::name), Codecs.BASIC_OBJECT.listOf().fieldOf("values").forGetter(ConfigData::objects)).apply(instance, ConfigData::new));
    }
}
