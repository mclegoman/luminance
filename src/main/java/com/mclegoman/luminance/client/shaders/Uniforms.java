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
import com.mclegoman.luminance.client.shaders.uniforms.RootUniform;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.DeltaUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.PrevUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.SmoothUniform;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.Accessors;
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
		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.tick());
	}

	public static void update() {
		float tickDelta = ClientData.minecraft.getRenderTickCounter().getTickDelta(true);
		float deltaTime = ((tickDelta < prevTickDelta ? 1 : 0) + tickDelta-prevTickDelta);
		prevTickDelta = tickDelta;

		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.update(tickDelta, deltaTime));
	}

	public static void init() {
		try {
			String path = Data.version.getID();
			registerStandardTree(path, "viewDistance", Uniforms::getViewDistance, 2f, null);
			registerStandardTree(path, "fov", Uniforms::getFov, 0f, 360f);
			registerStandardTree(path, "fps", Uniforms::getFps, 0f, null);
			registerStandardTree(path, "eyeX", Uniforms::getEyeX, null, null);
			registerStandardTree(path, "eyeY", Uniforms::getEyeY, null, null);
			registerStandardTree(path, "eyeZ", Uniforms::getEyeZ, null, null);
			registerStandardTree(path, "posX", Uniforms::getPosX, null, null);
			registerStandardTree(path, "posY", Uniforms::getPosY, null, null);
			registerStandardTree(path, "posZ", Uniforms::getPosZ, null, null);
			registerStandardTree(path, "pitch", Uniforms::getPitch, -90f, 90f);
			registerStandardTree(path, "yaw", Uniforms::getYaw, -180f, 180f);
			registerStandardTree(path, "currentHealth", Uniforms::getCurrentHealth, 0f, null);
			registerStandardTree(path, "maxHealth", Uniforms::getMaxHealth, 0f, null);
			registerStandardTree(path, "currentAbsorption", Uniforms::getCurrentAbsorption, 0f, null);
			registerStandardTree(path, "maxAbsorption", Uniforms::getMaxAbsorption, 0f, null);
			registerStandardTree(path, "currentHurtTime", Uniforms::getCurrentHurtTime, 0f, null);
			registerStandardTree(path, "maxHurtTime", Uniforms::getMaxHurtTime, 0f, null);
			registerStandardTree(path, "currentAir", Uniforms::getCurrentAir, 0f, null);
			registerStandardTree(path, "maxAir", Uniforms::getMaxAir, 0f, null);
			registerStandardTree(path, "isAlive", Uniforms::getIsAlive, 0f, 1f);
			registerStandardTree(path, "isDead", Uniforms::getIsDead, 0f, 1f);
			registerStandardTree(path, "isSprinting", Uniforms::getIsSprinting, 0f, 1f);
			registerStandardTree(path, "isSwimming", Uniforms::getIsSwimming, 0f, 1f);
			registerStandardTree(path, "isSneaking", Uniforms::getIsSneaking, 0f, 1f);
			registerStandardTree(path, "isCrawling", Uniforms::getIsCrawling, 0f, 1f);
			registerStandardTree(path, "isInvisible", Uniforms::getIsInvisible, 0f, 1f);
			registerStandardTree(path, "isWithered", (tickDelta, deltaTime) -> Uniforms.getHasEffect(StatusEffects.WITHER), 0f, 1f);
			registerStandardTree(path, "isPoisoned", (tickDelta, deltaTime) -> Uniforms.getHasEffect(StatusEffects.POISON), 0f, 1f);
			registerStandardTree(path, "isBurning", Uniforms::getIsBurning, 0f, 1f);
			registerStandardTree(path, "isOnGround", Uniforms::getIsOnGround, 0f, 1f);
			registerStandardTree(path, "isOnLadder", Uniforms::getIsOnLadder, 0f, 1f);
			registerStandardTree(path, "isRiding", Uniforms::getIsRiding, 0f, 1f);
			registerStandardTree(path, "hasPassengers", Uniforms::getHasPassengers, 0f, 1f);
			registerStandardTree(path, "biomeTemperature", Uniforms::getBiomeTemperature, 0f, 1f);
			registerStandardTree(path, "alpha", Uniforms::getAlpha, 0f, 1f);
			registerStandardTree(path, "perspective", Uniforms::getPerspective, 0f, 3f);
			registerStandardTree(path, "selectedSlot", Uniforms::getSelectedSlot, 0f, 8f);
			registerStandardTree(path, "score", Uniforms::getScore, 0f, null);
			registerStandardTree(path, "velocity", Uniforms::getVelocity, 0f, null);
			registerStandardTree(path, "skyAngle", Uniforms::getSkyAngle, 0f, 1f);
			registerStandardTree(path, "sunAngle", Uniforms::getSunAngle, 0f ,1f);
			registerStandardTree(path, "isDay", Uniforms::getIsDay, 0f, 1f);
			registerStandardTree(path, "starBrightness", Uniforms::getStarBrightness, 0f, 1f);

			// Time Uniform should be updated to be customizable.
			registerStandardTree(path, "gameTime", Uniforms::getGameTime, 0f, 1f);
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to initialize uniforms: {}", error));
		}
	}

	public static void registerStandardTree(String path, String name, Callables.ShaderRender<Float> callable, @Nullable Float min, @Nullable Float max) {
		registerTree(path, new RootUniform(callable, min, max, name).addChildren(new DeltaUniform(), new PrevUniform(), new SmoothUniform().addChildren(new DeltaUniform(), new PrevUniform())));
	}

	public static void registerTree(String path, TreeUniform treeUniform) {
		String name = path+"_"+treeUniform.name;
		Events.ShaderUniform.register(name, treeUniform);
		for (TreeUniform child : treeUniform.children) {
			registerTree(name, child);
		}
	}

	public static float getViewDistance(float tickDelta, float deltaTime) {
		return ClientData.minecraft.options != null ? ClientData.minecraft.options.getViewDistance().getValue() : 12.0F;
	}
	public static float getFov(float tickDelta, float deltaTime) {
		return Accessors.getGameRenderer() != null ? Accessors.getGameRenderer().invokeGetFov(ClientData.minecraft.gameRenderer.getCamera(), tickDelta, false) : 70.0F;
	}
	public static float getFps(float tickDelta, float deltaTime) {
		return ClientData.minecraft.getCurrentFps();
	}
	// TODO: Make Time Uniform be configurable (or moreso, all uniforms).
	private static float time = 0f;
	private static float prevTickDelta = 0.0F;
	public static float getGameTime(float tickDelta, float deltaTime) {
		// Ideally, this would be customisable using a config, but this works for now.
		// Could we add something like this to the post/x.json and program/x.json files?
		// options {
		//     "luminance_time": 20
		// }
		// NOTE: adding data to the json isn't actually that bad, see PostEffectPipelineMixin

		time += deltaTime/24000f;
		if (time > 1) time -= 1;
		return time;
	}
	public static float getEyeX(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getEyePos().x : 0.0F;
	}
	public static float getEyeY(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getEyePos().y : 66.0F;
	}
	public static float getEyeZ(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getEyePos().z : 0.0F;
	}
	public static float getPosX(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getPos().x :0.0F;
	}
	public static float getPosY(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getPos().y : 64.0F;
	}
	public static float getPosZ(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (float) ClientData.minecraft.player.getPos().z : 0.0F;
	}
	public static float getPitch(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getPitch(tickDelta) % 360.0F : 0.0F;
	}
	public static float getYaw(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? MathHelper.floorMod(ClientData.minecraft.player.getYaw(tickDelta)+180f,360.0F)-180f : 0.0F;
	}
	public static float getCurrentHealth(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getHealth() : 20.0F;
	}
	public static float getMaxHealth(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxHealth() : 20.0F;
	}
	public static float getCurrentAbsorption(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getAbsorptionAmount() : 0.0F;
	}
	public static float getMaxAbsorption(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAbsorption() : 0.0F;
	}
	public static float getCurrentHurtTime(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.hurtTime : 0.0F;
	}
	public static float getMaxHurtTime(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.maxHurtTime : 10.0F;
	}
	public static float getCurrentAir(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getAir() : 10.0F;
	}
	public static float getMaxAir(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAir() : 10.0F;
	}
	public static float getIsAlive(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isAlive() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsDead(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isDead() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSprinting(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSprinting() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSwimming(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSwimming() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSneaking(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSneaking() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsCrawling(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isCrawling() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsInvisible(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isInvisible() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getHasEffect(RegistryEntry<StatusEffect> statusEffect) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasStatusEffect(statusEffect) ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsBurning(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnFire() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsOnGround(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnGround() ? 1.0F : 0.0F) : 1.0F;
	}
	public static float getIsOnLadder(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isHoldingOntoLadder() ? 1.0F : 0.0F) : 1.0F;
	}
	public static float getIsRiding(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isRiding() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getHasPassengers(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasPassengers() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getBiomeTemperature(float tickDelta, float deltaTime) {
		return ClientData.minecraft.world != null && ClientData.minecraft.player != null ? ClientData.minecraft.world.getBiome(ClientData.minecraft.player.getBlockPos()).value().getTemperature() : 1.0F;
	}
	public static float getAlpha(float tickDelta, float deltaTime) {
		return Math.clamp(getRawAlpha() / 100.0F, 0.0F, 1.0F);
	}
	public static int getRawAlpha() {
		return LuminanceConfig.config.alphaLevel.value();
	}
	public static void setAlpha(int value) {
		LuminanceConfig.config.alphaLevel.setValue(Math.clamp(value, 0, 100), false);
		alphaLevelOverlay();
	}
	public static void resetAlpha() {
		setAlpha(100);
	}
	public static void adjustAlpha(int amount) {
		setAlpha(getRawAlpha() + amount);
	}
	private static void alphaLevelOverlay() {
		if (LuminanceConfig.config.showAlphaLevelOverlay.value()) MessageOverlay.setOverlay(Translation.getTranslation(Data.version.getID(), "alpha_level", new Object[]{getRawAlpha() + "%"}, new Formatting[]{Formatting.GOLD}));
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
	public static float getPerspective(float tickDelta, float deltaTime) {
		if (ClientData.minecraft.options != null) {
			Perspective perspective = ClientData.minecraft.options.getPerspective();
			return perspective.equals(Perspective.THIRD_PERSON_FRONT) ? 3.0F : (perspective.equals(Perspective.THIRD_PERSON_BACK) ? 2.0F : (perspective.equals(Perspective.FIRST_PERSON) ? 1.0F : 0.0F));
		}
		return 1.0F;
	}
	public static float getSelectedSlot(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getInventory().selectedSlot : 0.0F;
	}
	public static float getScore(float tickDelta, float deltaTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getScore() : 0.0F;
	}
	public static float getVelocity(float tickDelta, float deltaTime) {
		if (ClientData.minecraft.player != null) {
			float x = (float) (ClientData.minecraft.player.getX() - ClientData.minecraft.player.prevX);
			float y = (float) (ClientData.minecraft.player.getY() - ClientData.minecraft.player.prevY);
			float z = (float) (ClientData.minecraft.player.getZ() - ClientData.minecraft.player.prevZ);
			return (float) Math.sqrt(x * x + y * y + z * z);
		}
		return 0.0F;
	}
	public static float getSkyAngle(float tickDelta, float deltaTime) {
		return ClientData.minecraft.world != null ? ClientData.minecraft.world.getSkyAngle(tickDelta) : 0.0F;
	}
	public static float getSunAngle(float tickDelta, float deltaTime) {
		float skyAngle = getSkyAngle(tickDelta, deltaTime);
		return skyAngle < 0.75F ? skyAngle + 0.25F : skyAngle - 0.75F;
	}
	public static float getIsDay(float tickDelta, float deltaTime) {
		return (getSunAngle(tickDelta, deltaTime) <= 0.5) ? 1.0F : 0.0F;
	}
	public static float getStarBrightness(float tickDelta, float deltaTime) {
		return ClientData.minecraft.world != null ? ClientData.minecraft.world.getStarBrightness(tickDelta) : 0.0F;
	}
}
