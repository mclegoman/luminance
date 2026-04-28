/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.debug;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.translation.Translation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DebugEntryDebugShader implements DebugScreenEntry {
    private static final Identifier groupId = LuminanceClient.getMod().idOf("debug");

    public void display(DebugScreenDisplayer debugScreenDisplayer, @Nullable Level level, @Nullable LevelChunk levelChunk, @Nullable LevelChunk levelChunk2) {
        debugScreenDisplayer.addToGroup(groupId, Translation.getString("{} v{}", LuminanceClient.getMod().getMetadata().getName(), LuminanceClient.getMod().getMetadata().getVersion().getFriendlyString()));
        if (ClientData.isDevelopment()) {
            debugScreenDisplayer.addToGroup(groupId, "%sDebug Shader".formatted(ChatFormatting.BOLD));
            debugScreenDisplayer.addToGroup(groupId, Translation.getString("Enabled: {}", Debug.isDebugShaderEnabled()));
            debugScreenDisplayer.addToGroup(groupId, Translation.getString("Render Location: {}", Debug.debugRenderLocation.identifier()));
            debugScreenDisplayer.addToGroup(groupId, Translation.getString("Stack Registry: {}", Debug.getDebugShader().getFirst()));
            debugScreenDisplayer.addToGroup(groupId, Translation.getString("Shader Stack: {}", Debug.getDebugShader().getSecond()));
        }
    }

    public boolean isAllowed(boolean bl) {
        return true;
    }
}