/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.uniforms.LuminanceUniform;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.Accessors;
import com.mclegoman.luminance.client.util.LuminanceIdentifier;
import com.mclegoman.luminance.client.util.MessageOverlay;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class Uniforms {
	private static int prevAlpha = getRawAlpha();
	public static void tick() {
		if (!updatingAlpha() && updatingAlpha) {
			updatingAlpha = false;
			if (getRawAlpha() != prevAlpha) LuminanceConfig.config.save();
		}
		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.tick(ClientData.minecraft.getRenderTickCounter().getTickDelta(true)));
	}
	public static void init() {
		try {
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("viewDistance"), Uniforms::getViewDistance, 2f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("fov"), Uniforms::getFov, 0f, 360f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("fps"), Uniforms::getFps, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("eyeX"), Uniforms::getEyeX, null, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("eyeY"), Uniforms::getEyeY, null, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("eyeZ"), Uniforms::getEyeZ, null, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("posX"), Uniforms::getPosX, null, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("posY"), Uniforms::getPosY, null, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("posZ"), Uniforms::getPosZ, null, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("pitch"), Uniforms::getPitch, -90f, 90f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("yaw"), Uniforms::getYaw, -180f, 180f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("currentHealth"), Uniforms::getCurrentHealth, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("maxHealth"), Uniforms::getMaxHealth, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("currentAbsorption"), Uniforms::getCurrentAbsorption, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("maxAbsorption"), Uniforms::getMaxAbsorption, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("currentHurtTime"), Uniforms::getCurrentHurtTime, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("maxHurtTime"), Uniforms::getMaxHurtTime, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("currentAir"), Uniforms::getCurrentAir, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("maxAir"), Uniforms::getMaxAir, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isAlive"), Uniforms::getIsAlive, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isDead"), Uniforms::getIsDead, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isSprinting"), Uniforms::getIsSprinting, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isSwimming"), Uniforms::getIsSwimming, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isSneaking"), Uniforms::getIsSneaking, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isCrawling"), Uniforms::getIsCrawling, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isInvisible"), Uniforms::getIsInvisible, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isWithered"), (tickDelta) -> Uniforms.getHasEffect(StatusEffects.WITHER), 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isPoisoned"), (tickDelta) -> Uniforms.getHasEffect(StatusEffects.POISON), 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isBurning"), Uniforms::getIsBurning, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isOnGround"), Uniforms::getIsOnGround, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isOnLadder"), Uniforms::getIsOnLadder, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isRiding"), Uniforms::getIsRiding, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("hasPassengers"), Uniforms::getHasPassengers, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("biomeTemperature"), Uniforms::getBiomeTemperature, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("alpha"), Uniforms::getAlpha, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("perspective"), Uniforms::getPerspective, 0f, 3f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("selectedSlot"), Uniforms::getSelectedSlot, 0f, 8f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("score"), Uniforms::getScore, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("velocity"), Uniforms::getVelocity, 0f, null);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("skyAngle"), Uniforms::getSkyAngle, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("sunAngle"), Uniforms::getSunAngle, 0f ,1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("isDay"), Uniforms::getIsDay, 0f, 1f);
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("starBrightness"), Uniforms::getStarBrightness, 0f, 1f);

			// Time Uniform should be updated to be customizable.
			registerLuminanceUniform(LuminanceIdentifier.ofLuminance("gameTime"), Uniforms::getGameTime, 0f, 1f);
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to initialize uniforms: {}", error));
		}
	}
	public static void registerLuminanceUniform(LuminanceIdentifier luminanceIdentifier, Callables.ShaderRender<Float> callable, @Nullable Float min, @Nullable Float max) {
		Events.ShaderUniform.register(luminanceIdentifier, new LuminanceUniform(callable, min, max));
	}
	public static float getViewDistance(float tickDelta) {
		return ClientData.minecraft.options != null ? ClientData.minecraft.options.getViewDistance().getValue() : 12.0F;
	}
	public static float getFov(float tickDelta) {
		return Accessors.getGameRenderer() != null ? Accessors.getGameRenderer().invokeGetFov(ClientData.minecraft.gameRenderer.getCamera(), tickDelta, false) : 70.0F;
	}
	public static float getFps(float tickDelta) {
		return ClientData.minecraft.getCurrentFps();
	}
	// TODO: Make Time Uniform be configurable (or moreso, all uniforms).
	private static float time = 0f;
	private static float prevTickDelta = 0.0F;
	public static float getGameTime(float tickDelta) {
		// Ideally, this would be customisable using a config, but this works for now.
		// Could we add something like this to the post/x.json and program/x.json files?
		// options {
		//     "luminance_time": 20
		// }
		// NOTE: adding data to the json isn't actually that bad, see PostEffectPipelineMixin

		time += ((tickDelta < prevTickDelta ? 1 : 0) + tickDelta-prevTickDelta)/24000f;
		if (time > 1) time -= 1;
		prevTickDelta = tickDelta;
		return time;
	}
	public static float getEyeX(float tickDelta) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getEyePos().x : 0.0F;
	}
	public static float getEyeY(float tickDelta) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getEyePos().y : 66.0F;
	}
	public static float getEyeZ(float tickDelta) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getEyePos().z : 0.0F;
	}
	public static float getPosX(float tickDelta) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getPos().x :0.0F;
	}
	public static float getPosY(float tickDelta) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getPos().y : 64.0F;
	}
	public static float getPosZ(float tickDelta) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getPos().z : 0.0F;
	}
	public static float getPitch(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getPitch(tickDelta) % 360.0F : 0.0F;
	}
	public static float getYaw(float tickDelta) {
		return ClientData.minecraft.player != null ? MathHelper.floorMod(ClientData.minecraft.player.getYaw(tickDelta)+180f,360.0F)-180f : 0.0F;
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
	public static float getIsAlive(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isAlive() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsDead(float tickDelta) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isDead() ? 1.0F : 0.0F) : 0.0F;
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
		return Math.clamp(getRawAlpha() / 100.0F, 0.0F, 1.0F);
	}
	public static int getRawAlpha() {
		return LuminanceConfig.config.alpha_level.value();
	}
	public static void setAlpha(int value) {
		LuminanceConfig.config.alpha_level.setValue(Math.clamp(value, 0, 100), false);
		alphaLevelOverlay();
	}
	public static void resetAlpha() {
		setAlpha(100);
	}
	public static void adjustAlpha(int amount) {
		setAlpha(getRawAlpha() + amount);
	}
	private static void alphaLevelOverlay() {
		if (LuminanceConfig.config.show_alpha_level_overlay.value()) MessageOverlay.setOverlay(Translation.getTranslation(Data.version.getID(), "alpha_level", new Object[]{getRawAlpha() + "%"}, new Formatting[]{Formatting.GOLD}));
	}
	public static boolean updatingAlpha = false;
	public static boolean updatingAlpha() {
		boolean value = Keybindings.adjustAlpha.isPressed();
		if (value) {
			if (!updatingAlpha) {
				prevAlpha = getRawAlpha();
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
	public static float getSelectedSlot(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getInventory().selectedSlot : 0.0F;
	}
	public static float getScore(float tickDelta) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getScore() : 0.0F;
	}
	public static float getVelocity(float tickDelta) {
		if (ClientData.minecraft.player != null) {
			float x = (float) (ClientData.minecraft.player.getX() - ClientData.minecraft.player.prevX);
			float y = (float) (ClientData.minecraft.player.getY() - ClientData.minecraft.player.prevY);
			float z = (float) (ClientData.minecraft.player.getZ() - ClientData.minecraft.player.prevZ);
			return (float) Math.sqrt(x * x + y * y + z * z);
		}
		return 0.0F;
	}
	public static float getSkyAngle(float tickDelta) {
		return ClientData.minecraft.world != null ? ClientData.minecraft.world.getSkyAngle(tickDelta) : 0.0F;
	}
	public static float getSunAngle(float tickDelta) {
		float skyAngle = getSkyAngle(tickDelta);
		return skyAngle < 0.75F ? skyAngle + 0.25F : skyAngle - 0.75F;
	}
	public static float getIsDay(float tickDelta) {
		return (getSunAngle(tickDelta) <= 0.5) ? 1.0F : 0.0F;
	}
	public static float getStarBrightness(float tickDelta) {
		return ClientData.minecraft.world != null ? ClientData.minecraft.world.getStarBrightness(tickDelta) : 0.0F;
	}
}
