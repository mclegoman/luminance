/*
    Luminance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.Accessors;
import com.mclegoman.luminance.client.util.MessageOverlay;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mclegoman.luminance.config.ConfigHelper;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;

public class Uniforms {
	private static int prevAlpha = (int)ConfigHelper.getConfig("alpha_level");
	public static void tick() {
		Shaders.updateTime();
		if (!updatingAlpha() && updatingAlpha) {
			updatingAlpha = false;
			if ((int)ConfigHelper.getConfig("alpha_level") != prevAlpha) ConfigHelper.saveConfig(true);
		}
		SmoothUniforms.tick(ClientData.getTickDelta(true));
	}
	public static void init() {
		try {
			Events.ShaderUniform.registerFloat("lu", "viewDistance", Uniforms::getViewDistance);
			Events.ShaderUniform.registerFloat("lu", "fov", Uniforms::getFov);
			Events.ShaderUniform.registerFloat("lu", "fps", Uniforms::getFps);
			// Time Uniform should be updated to be customizable.
			Events.ShaderUniform.registerFloat("lu", "time", Uniforms::getTime);
			Events.ShaderUniform.registerFloats("lu", "eyePosition", Uniforms::getEyePosition);
			Events.ShaderUniform.registerFloats("lu", "position", Uniforms::getPosition);
			Events.ShaderUniform.registerFloat("lu", "pitch", Uniforms::getPitch);
			Events.ShaderUniform.registerFloat("lu", "yaw", Uniforms::getYaw);
			Events.ShaderUniform.registerFloat("lu", "currentHealth", Uniforms::getCurrentHealth);
			Events.ShaderUniform.registerFloat("lu", "maxHealth", Uniforms::getMaxHealth);
			Events.ShaderUniform.registerFloat("lu", "currentAbsorption", Uniforms::getCurrentAbsorption);
			Events.ShaderUniform.registerFloat("lu", "maxAbsorption", Uniforms::getMaxAbsorption);
			Events.ShaderUniform.registerFloat("lu", "currentHurtTime", Uniforms::getCurrentHurtTime);
			Events.ShaderUniform.registerFloat("lu", "maxHurtTime", Uniforms::getMaxHurtTime);
			Events.ShaderUniform.registerFloat("lu", "currentAir", Uniforms::getCurrentAir);
			Events.ShaderUniform.registerFloat("lu", "maxAir", Uniforms::getMaxAir);
			Events.ShaderUniform.registerFloat("lu", "isSprinting", Uniforms::getIsSprinting);
			Events.ShaderUniform.registerFloat("lu", "isSwimming", Uniforms::getIsSwimming);
			Events.ShaderUniform.registerFloat("lu", "isSneaking", Uniforms::getIsSneaking);
			Events.ShaderUniform.registerFloat("lu", "isCrawling", Uniforms::getIsCrawling);
			Events.ShaderUniform.registerFloat("lu", "isInvisible", Uniforms::getIsInvisible);
			Events.ShaderUniform.registerFloat("lu", "isWithered", (tickDelta) -> Uniforms.getHasEffect(StatusEffects.WITHER));
			Events.ShaderUniform.registerFloat("lu", "isPoisoned", (tickDelta) -> Uniforms.getHasEffect(StatusEffects.POISON));
			Events.ShaderUniform.registerFloat("lu", "isBurning", Uniforms::getIsBurning);
			Events.ShaderUniform.registerFloat("lu", "isOnGround", Uniforms::getIsOnGround);
			Events.ShaderUniform.registerFloat("lu", "isOnLadder", Uniforms::getIsOnLadder);
			Events.ShaderUniform.registerFloat("lu", "isRiding", Uniforms::getIsRiding);
			Events.ShaderUniform.registerFloat("lu", "hasPassengers", Uniforms::getHasPassengers);
			Events.ShaderUniform.registerFloat("lu", "biomeTemperature", Uniforms::getBiomeTemperature);
			Events.ShaderUniform.registerFloat("lu", "alpha", Uniforms::getAlpha);
			Events.ShaderUniform.registerFloat("lu", "perspective", Uniforms::getPerspective);
			SmoothUniforms.init();
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to initialize uniforms: {}", error));
		}
	}
	public static float getViewDistance(float tickDelta) {
		return ClientData.minecraft.options != null ? ClientData.minecraft.options.getViewDistance().getValue() : 12.0F;
	}
	public static float getFov(float tickDelta) {
		return Accessors.getGameRenderer() != null ? (float) Accessors.getGameRenderer().invokeGetFov(ClientData.minecraft.gameRenderer.getCamera(), ClientData.getTickDelta(true), false) : 70.0F;
	}
	public static float getFps(float tickDelta) {
		return ClientData.minecraft.getCurrentFps();
	}
	public static float getTime(float tickDelta) {
		return Shaders.time;
	}
	public static float[] getEyePosition(float tickDelta) {
		return ClientData.minecraft.player != null ? new float[]{(float) ClientData.minecraft.player.getEyePos().x, (float) ClientData.minecraft.player.getEyePos().y, (float) ClientData.minecraft.player.getEyePos().z} : new float[]{0.0F, 66.0F, 0.0F};
	}
	public static float[] getPosition(float tickDelta) {
		return ClientData.minecraft.player != null ? new float[]{(float) ClientData.minecraft.player.getPos().x, (float) ClientData.minecraft.player.getPos().y, (float) ClientData.minecraft.player.getPos().z} : new float[]{0.0F, 64.0F, 0.0F};
	}
	public static float getPitch(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getPitch(ClientData.getTickDelta(true)) % 360.0F : 0.0F;
	}
	public static float getYaw(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getYaw(ClientData.getTickDelta(true)) % 360.0F : 0.0F;
	}
	public static float getCurrentHealth(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getHealth() : 20.0F;
	}
	public static float getMaxHealth(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxHealth() : 20.0F;
	}
	public static float getCurrentAbsorption(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getAbsorptionAmount() : 0.0F;
	}
	public static float getMaxAbsorption(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAbsorption() : 0.0F;
	}
	public static float getCurrentHurtTime(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.hurtTime : 0.0F;
	}
	public static float getMaxHurtTime(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.maxHurtTime : 10.0F;
	}
	public static float getCurrentAir(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getAir() : 10.0F;
	}
	public static float getMaxAir(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAir() : 10.0F;
	}
	public static float getIsSprinting(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSprinting() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSwimming(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSwimming() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSneaking(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSneaking() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsCrawling(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isCrawling() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsInvisible(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isInvisible() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getHasEffect(RegistryEntry<StatusEffect> statusEffect) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasStatusEffect(statusEffect) ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsBurning(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnFire() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsOnGround(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnGround() ? 1.0F : 0.0F) : 1.0F;
	}
	public static float getIsOnLadder(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isHoldingOntoLadder() ? 1.0F : 0.0F) : 1.0F;
	}
	public static float getIsRiding(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isRiding() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getHasPassengers(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasPassengers() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getBiomeTemperature(float tickDelta) {
		return ClientData.minecraft.world != null && ClientData.minecraft.player != null ? ClientData.minecraft.world.getBiome(ClientData.minecraft.player.getBlockPos()).value().getTemperature() : 1.0F;
	}
	public static float getAlpha(float tickDelta) {
		return Math.max(0.0F, Math.min(((int)ConfigHelper.getConfig("alpha_level") / 100.0F), 1.0F));
	}
	public static void setAlpha(int value) {
		ConfigHelper.setConfig("alpha_level", Math.max(0, Math.min((value), 100)));
	}
	public static void resetAlpha() {
		setAlpha(100);
		if ((boolean)ConfigHelper.getConfig("show_alpha_level_overlay")) MessageOverlay.setOverlay(Translation.getTranslation(Data.version.getID(), "alpha_level", new Object[]{ConfigHelper.getConfig("alpha_level") + "%"}, new Formatting[]{Formatting.GOLD}));
	}
	public static void adjustAlpha(int amount) {
		setAlpha((int)ConfigHelper.getConfig("alpha_level") + amount);
		if ((boolean)ConfigHelper.getConfig("show_alpha_level_overlay")) MessageOverlay.setOverlay(Translation.getTranslation(Data.version.getID(), "alpha_level", new Object[]{ConfigHelper.getConfig("alpha_level") + "%"}, new Formatting[]{Formatting.GOLD}));
	}
	public static boolean updatingAlpha = false;
	public static boolean updatingAlpha() {
		boolean value = Keybindings.adjustAlpha.isPressed();
		if (value) {
			if (!updatingAlpha) {
				prevAlpha = (int)ConfigHelper.getConfig("alpha_level");
			}
			updatingAlpha = true;
		}
		return value;
	}
	public static float getPerspective(float tickDelta) {
		if (ClientData.minecraft.options != null) {
			Perspective perspective = ClientData.minecraft.options.getPerspective();
			return perspective.equals(Perspective.THIRD_PERSON_FRONT) ? 3.0F : (perspective.equals(Perspective.THIRD_PERSON_BACK) ? 2.0F : (perspective.equals(Perspective.FIRST_PERSON) ? 1.0F : 0.0F));
		}
		return 1.0F;
	}
}
