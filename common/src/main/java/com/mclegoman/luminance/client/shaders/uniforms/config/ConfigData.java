/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public record ConfigData(String name, List<Object> objects) {
    public static final Codec<ConfigData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.STRING.fieldOf("name").forGetter(ConfigData::name), Codecs.BASIC_OBJECT.listOf().fieldOf("values").forGetter(ConfigData::objects)).apply(instance, ConfigData::new));
}
