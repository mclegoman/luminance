package com.mclegoman.luminance.client.shaders.interfaces.internal;

import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public interface InternalPostChainInterface {
    void luminance$setCustomChains(Map<Identifier, List<PostPass>> customChains);
}
