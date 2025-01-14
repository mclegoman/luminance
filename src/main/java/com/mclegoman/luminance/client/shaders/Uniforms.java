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
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.shaders.uniforms.children.DeltaUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.ElementUniform;
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
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class Uniforms {
	public static ShaderTime shaderTime = new ShaderTime();

	private static int prevAlpha = getRawAlpha();
	public static void tick() {
		if (!updatingAlpha() && updatingAlpha) {
			updatingAlpha = false;
			if (getRawAlpha() != prevAlpha) LuminanceConfig.config.save();
		}
		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.tick());
	}

	public static void update() {
		shaderTime.update(ClientData.minecraft.getRenderTickCounter().getTickDelta(true));
		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.update(shaderTime));
	}

	public static void init() {
		try {
			String path = Data.version.getID();
			registerSingleTree(path, "viewDistance", Uniforms::getViewDistance, 2f, null);
			registerSingleTree(path, "fov", Uniforms::getFov, 0f, 360f);
			registerSingleTree(path, "fps", Uniforms::getFps, 0f, null);

			registerStandardTree(path, "eye", Uniforms::getEye, null, null, 3, false);
			registerStandardTree(path, "pos", Uniforms::getPos, null, null, 3, false);

			registerSingleTree(path, "pitch", Uniforms::getPitch, -90f, 90f);
			registerSingleTree(path, "yaw", Uniforms::getYaw, -180f, 180f);
			registerSingleTree(path, "currentHealth", Uniforms::getCurrentHealth, 0f, null);
			registerSingleTree(path, "maxHealth", Uniforms::getMaxHealth, 0f, null);
			registerSingleTree(path, "currentAbsorption", Uniforms::getCurrentAbsorption, 0f, null);
			registerSingleTree(path, "maxAbsorption", Uniforms::getMaxAbsorption, 0f, null);
			registerSingleTree(path, "currentHurtTime", Uniforms::getCurrentHurtTime, 0f, null);
			registerSingleTree(path, "maxHurtTime", Uniforms::getMaxHurtTime, 0f, null);
			registerSingleTree(path, "currentAir", Uniforms::getCurrentAir, 0f, null);
			registerSingleTree(path, "maxAir", Uniforms::getMaxAir, 0f, null);
			registerSingleTree(path, "isAlive", Uniforms::getIsAlive, 0f, 1f);
			registerSingleTree(path, "isDead", Uniforms::getIsDead, 0f, 1f);
			registerSingleTree(path, "isSprinting", Uniforms::getIsSprinting, 0f, 1f);
			registerSingleTree(path, "isSwimming", Uniforms::getIsSwimming, 0f, 1f);
			registerSingleTree(path, "isSneaking", Uniforms::getIsSneaking, 0f, 1f);
			registerSingleTree(path, "isCrawling", Uniforms::getIsCrawling, 0f, 1f);
			registerSingleTree(path, "isInvisible", Uniforms::getIsInvisible, 0f, 1f);
			registerSingleTree(path, "isWithered", (shaderTime) -> Uniforms.getHasEffect(StatusEffects.WITHER), 0f, 1f);
			registerSingleTree(path, "isPoisoned", (shaderTime) -> Uniforms.getHasEffect(StatusEffects.POISON), 0f, 1f);
			registerSingleTree(path, "isBurning", Uniforms::getIsBurning, 0f, 1f);
			registerSingleTree(path, "isOnGround", Uniforms::getIsOnGround, 0f, 1f);
			registerSingleTree(path, "isOnLadder", Uniforms::getIsOnLadder, 0f, 1f);
			registerSingleTree(path, "isRiding", Uniforms::getIsRiding, 0f, 1f);
			registerSingleTree(path, "hasPassengers", Uniforms::getHasPassengers, 0f, 1f);
			registerSingleTree(path, "biomeTemperature", Uniforms::getBiomeTemperature, 0f, 1f);
			registerSingleTree(path, "alpha", Uniforms::getAlpha, 0f, 1f);
			registerSingleTree(path, "perspective", Uniforms::getPerspective, 0f, 3f);
			registerSingleTree(path, "selectedSlot", Uniforms::getSelectedSlot, 0f, 8f);
			registerSingleTree(path, "score", Uniforms::getScore, 0f, null);
			registerSingleTree(path, "velocity", Uniforms::getVelocity, 0f, null);
			registerSingleTree(path, "skyAngle", Uniforms::getSkyAngle, 0f, 1f);
			registerSingleTree(path, "sunAngle", Uniforms::getSunAngle, 0f ,1f);
			registerSingleTree(path, "isDay", Uniforms::getIsDay, 0f, 1f);
			registerSingleTree(path, "starBrightness", Uniforms::getStarBrightness, 0f, 1f);

			// Time Uniform should be updated to be customizable.
			registerStandardTree(path, "time", Uniforms::getGameTime, 0f, 1f, 1, true);
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to initialize uniforms: {}", error));
		}
	}

	public static void registerSingleTree(String path, String name, Callables.SingleUniformCalculation callable, @Nullable Float min, @Nullable Float max) {
		registerStandardTree(path, name, callable.convert(), min, max, 1, false);
	}

	public static void registerStandardTree(String path, String name, @Nullable Callables.UniformCalculation callable, @Nullable Float min, @Nullable Float max, int length, boolean useConfig) {
		RootUniform uniform = new RootUniform(name, callable, length, useConfig, UniformValue.fromFloat(min, length), UniformValue.fromFloat(max, length));
		if (!useConfig) {
			addStandardChildren(uniform, length);
		}
		registerTree(path, uniform);
	}

	public static void registerTree(String path, TreeUniform treeUniform) {
		String name = path+"_"+treeUniform.name;
		treeUniform.onRegister(name);
		Events.ShaderUniform.register(name, treeUniform);
		for (TreeUniform child : treeUniform.children) {
			registerTree(name, child);
		}
	}

	public static TreeUniform addStandardChildren(TreeUniform treeUniform, int length) {
		addElementChildren(treeUniform.addChildren(addElementChildren(new DeltaUniform(), length), addElementChildren(new PrevUniform(), length), new SmoothUniform().addChildren(addElementChildren(new DeltaUniform(), length), addElementChildren(new PrevUniform(), length))), length);
		return treeUniform;
	}

	public static TreeUniform addElementChildren(TreeUniform treeUniform, int length) {
		if (length == 2) {
			treeUniform.addChildren(new ElementUniform("x", 0), new ElementUniform("y", 1));
		} else if (length == 3) {
			treeUniform.addChildren(new ElementUniform("x", 0), new ElementUniform("y", 1), new ElementUniform("z", 2));
		} else if (length == 4) {
			treeUniform.addChildren(new ElementUniform("x", 0), new ElementUniform("y", 1), new ElementUniform("z", 2), new ElementUniform("w", 3));
		}
		return treeUniform;
	}

	public static float getViewDistance(ShaderTime shaderTime) {
		return ClientData.minecraft.options != null ? ClientData.minecraft.options.getViewDistance().getValue() : 12.0F;
	}
	public static float getFov(ShaderTime shaderTime) {
		return Accessors.getGameRenderer() != null ? Accessors.getGameRenderer().invokeGetFov(ClientData.minecraft.gameRenderer.getCamera(), shaderTime.getTickDelta(), false) : 70.0F;
	}
	public static float getFps(ShaderTime shaderTime) {
		return ClientData.minecraft.getCurrentFps();
	}

	public static void getGameTime(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		float period = config.getOrDefault("period", 0, 1f).floatValue();
		uniformValue.set(0, shaderTime.getModuloTime(period)/period);
	}

	public static void getEye(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(ClientData.minecraft.player.getEyePos());
		} else {
			uniformValue.set(new Vec3d(0, 66, 0));
		}
	}
	public static void getPos(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(ClientData.minecraft.player.getPos());
		} else {
			uniformValue.set(new Vec3d(0, 64, 0));
		}
	}


	public static float getPitch(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getPitch(shaderTime.getTickDelta()) % 360.0F : 0.0F;
	}
	public static float getYaw(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? MathHelper.floorMod(ClientData.minecraft.player.getYaw(shaderTime.getTickDelta())+180f,360.0F)-180f : 0.0F;
	}
	public static float getCurrentHealth(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getHealth() : 20.0F;
	}
	public static float getMaxHealth(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxHealth() : 20.0F;
	}
	public static float getCurrentAbsorption(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getAbsorptionAmount() : 0.0F;
	}
	public static float getMaxAbsorption(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAbsorption() : 0.0F;
	}
	public static float getCurrentHurtTime(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.hurtTime : 0.0F;
	}
	public static float getMaxHurtTime(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.maxHurtTime : 10.0F;
	}
	public static float getCurrentAir(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getAir() : 10.0F;
	}
	public static float getMaxAir(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAir() : 10.0F;
	}
	public static float getIsAlive(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isAlive() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsDead(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isDead() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSprinting(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSprinting() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSwimming(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSwimming() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsSneaking(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSneaking() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsCrawling(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isCrawling() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsInvisible(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isInvisible() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getHasEffect(RegistryEntry<StatusEffect> statusEffect) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasStatusEffect(statusEffect) ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsBurning(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnFire() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsOnGround(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnGround() ? 1.0F : 0.0F) : 1.0F;
	}
	public static float getIsOnLadder(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isHoldingOntoLadder() ? 1.0F : 0.0F) : 1.0F;
	}
	public static float getIsRiding(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isRiding() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getHasPassengers(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasPassengers() ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getBiomeTemperature(ShaderTime shaderTime) {
		return ClientData.minecraft.world != null && ClientData.minecraft.player != null ? ClientData.minecraft.world.getBiome(ClientData.minecraft.player.getBlockPos()).value().getTemperature() : 1.0F;
	}
	public static float getAlpha(ShaderTime shaderTime) {
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
	public static float getPerspective(ShaderTime shaderTime) {
		if (ClientData.minecraft.options != null) {
			Perspective perspective = ClientData.minecraft.options.getPerspective();
			return perspective.equals(Perspective.THIRD_PERSON_FRONT) ? 3.0F : (perspective.equals(Perspective.THIRD_PERSON_BACK) ? 2.0F : (perspective.equals(Perspective.FIRST_PERSON) ? 1.0F : 0.0F));
		}
		return 1.0F;
	}
	public static float getSelectedSlot(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getInventory().selectedSlot : 0.0F;
	}
	public static float getScore(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getScore() : 0.0F;
	}
	public static float getVelocity(ShaderTime shaderTime) {
		if (ClientData.minecraft.player != null) {
			float x = (float) (ClientData.minecraft.player.getX() - ClientData.minecraft.player.prevX);
			float y = (float) (ClientData.minecraft.player.getY() - ClientData.minecraft.player.prevY);
			float z = (float) (ClientData.minecraft.player.getZ() - ClientData.minecraft.player.prevZ);
			return (float) Math.sqrt(x * x + y * y + z * z);
		}
		return 0.0F;
	}
	public static float getSkyAngle(ShaderTime shaderTime) {
		return ClientData.minecraft.world != null ? ClientData.minecraft.world.getSkyAngle(shaderTime.getTickDelta()) : 0.0F;
	}
	public static float getSunAngle(ShaderTime shaderTime) {
		float skyAngle = getSkyAngle(shaderTime);
		return skyAngle < 0.75F ? skyAngle + 0.25F : skyAngle - 0.75F;
	}
	public static float getIsDay(ShaderTime shaderTime) {
		return (getSunAngle(shaderTime) <= 0.5) ? 1.0F : 0.0F;
	}
	public static float getStarBrightness(ShaderTime shaderTime) {
		return ClientData.minecraft.world != null ? ClientData.minecraft.world.getStarBrightness(shaderTime.getTickDelta()) : 0.0F;
	}
}
