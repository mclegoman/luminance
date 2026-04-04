/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.gui.screen.LuminanceTitleScreen;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.uniforms.RootUniform;
import com.mclegoman.luminance.client.shaders.uniforms.TreeUniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.shaders.uniforms.children.DeltaUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.ElementUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.PrevUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.SmoothUniform;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.MapConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.client.util.Accessors;
import com.mclegoman.luminance.client.util.MessageOverlay;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mclegoman.luminance.mixin.client.shaders.DynamicRenderTickCounterAccessor;
import com.mclegoman.luminance.mixin.client.shaders.GameRendererAccessor;
import com.mclegoman.luminance.mixin.client.shaders.WorldRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
		shaderTime.update(((DynamicRenderTickCounterAccessor)ClientData.minecraft.getRenderTickCounter()).getRawTickProgress());
		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.update(shaderTime));
	}
	public static void init() {
		try {
			String namespace = Data.getVersion().getID();

			registerSingleTree(namespace, "panorama_alpha", Uniforms::getPanoramaAlpha, 0f, 1f);
			registerSingleTree(namespace, "hud_hidden", Uniforms::getHudHidden, 0f, 1f);
			registerSingleTree(namespace, "is_in_gui", Uniforms::getIsInGui, 0f, 1f);
			registerSingleTree(namespace, "view_distance", Uniforms::getViewDistance, 2f, null);
			registerSingleTree(namespace, "fov", Uniforms::getFov, 0f, 360f);
			registerSingleTree(namespace, "fps", Uniforms::getFps, 0f, null);
			registerStandardTree(namespace, "graphics_mode", Uniforms::getGraphicsMode, 0f, 2f, 1, EmptyConfig.INSTANCE, false);
			registerStandardTree(namespace, "eye", Uniforms::getEye, null, null, 3, null, false);
			registerStandardTree(namespace, "eye_fract", Uniforms::getEyeFract, 0f, 1f, 3, null, true);
			registerStandardTree(namespace, "pos", Uniforms::getPos, null, null, 3, null, false);
			registerStandardTree(namespace, "pos_fract", Uniforms::getPosFract, 0f, 1f, 3, null,  true);
			registerStandardTree(namespace, "cam", Uniforms::getCamera, null, null, 3, EmptyConfig.INSTANCE, false);
			registerStandardTree(namespace, "cam_fract", Uniforms::getCameraFract, 0f, 1f, 3, EmptyConfig.INSTANCE,  true);
			registerSingleTree(namespace, "pitch", Uniforms::getPitch, -90f, 90f);
			registerStandardTree(namespace, "yaw", Uniforms::getYaw, -180f, 180f, 1, null, true);
			registerStandardTree(namespace, "clipping", Uniforms::getClippingPlanes, 0f, null, 2, null, false);
			registerSingleTree(namespace, "velocity", Uniforms::getVelocity, 0f, null);
			registerRangedTree(namespace, "current_health", Uniforms::getCurrentHealth, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxHealth(b)), 1, null, false);
			registerSingleTree(namespace, "max_health", Uniforms::getMaxHealth, 0f, null);
			registerRangedTree(namespace, "current_absorption", Uniforms::getCurrentAbsorption, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxAbsorption(b)), 1, null, false);
			registerSingleTree(namespace, "max_absorption", Uniforms::getMaxAbsorption, 0f, null);
			registerRangedTree(namespace, "current_hurt_time", Uniforms::getCurrentHurtTime, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxHurtTime(b)), 1, null, false);
			registerSingleTree(namespace, "max_hurt_time", Uniforms::getMaxHurtTime, 0f, null);
			registerRangedTree(namespace, "current_air", Uniforms::getCurrentAir, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxAir(b)), 1, null, false);
			registerSingleTree(namespace, "max_air", Uniforms::getMaxAir, 0f, null);
			registerSingleTree(namespace, "is_alive", Uniforms::getIsAlive, 0f, 1f);
			registerSingleTree(namespace, "is_dead", Uniforms::getIsDead, 0f, 1f);
			registerSingleTree(namespace, "is_sprinting", Uniforms::getIsSprinting, 0f, 1f);
			registerSingleTree(namespace, "is_swimming", Uniforms::getIsSwimming, 0f, 1f);
			registerSingleTree(namespace, "is_sneaking", Uniforms::getIsSneaking, 0f, 1f);
			registerSingleTree(namespace, "is_crawling", Uniforms::getIsCrawling, 0f, 1f);
			registerSingleTree(namespace, "is_invisible", Uniforms::getIsInvisible, 0f, 1f);
			registerSingleTree(namespace, "is_withered", (shaderTime) -> Uniforms.getHasEffect(StatusEffects.WITHER), 0f, 1f);
			registerSingleTree(namespace, "is_poisoned", (shaderTime) -> Uniforms.getHasEffect(StatusEffects.POISON), 0f, 1f);
			registerStandardTree(namespace, "is_in_biome", Uniforms::getIsInBiome, 0f, 1f, 1, new MapConfig(List.of(new ConfigData("biome", List.of("minecraft:plains")))), false);
			registerStandardTree(namespace, "effect_duration", Uniforms::getEffectDuration, null, null, 1, new MapConfig(List.of(new ConfigData("effect", List.of("minecraft:speed")))), false);
			registerStandardTree(namespace, "effect_amplifier", Uniforms::getEffectAmplifier, 0f, 255f, 1, new MapConfig(List.of(new ConfigData("effect", List.of("minecraft:speed")))), false);
			registerSingleTree(namespace, "is_burning", Uniforms::getIsBurning, 0f, 1f);
			registerSingleTree(namespace, "is_on_ground", Uniforms::getIsOnGround, 0f, 1f);
			registerSingleTree(namespace, "is_on_ladder", Uniforms::getIsOnLadder, 0f, 1f);
			registerSingleTree(namespace, "is_riding", Uniforms::getIsRiding, 0f, 1f);
			registerSingleTree(namespace, "has_passengers", Uniforms::getHasPassengers, 0f, 1f);
			registerSingleTree(namespace, "biome_temperature", Uniforms::getBiomeTemperature, 0f, 1f);
			registerSingleTree(namespace, "alpha", Uniforms::getAlpha, 0f, 1f);
			registerSingleTree(namespace, "perspective", Uniforms::getPerspective, 0f, 3f);
			registerSingleTree(namespace, "selected_slot", Uniforms::getSelectedSlot, 0f, 8f);
			registerSingleTree(namespace, "score", Uniforms::getScore, 0f, null);
			registerSingleTree(namespace, "sky_angle", Uniforms::getSkyAngle, 0f, 1f);
			registerSingleTree(namespace, "sun_angle", Uniforms::getSunAngle, 0f ,1f);
			registerSingleTree(namespace, "is_day", Uniforms::getIsDay, 0f, 1f);
			registerSingleTree(namespace, "star_brightness", Uniforms::getStarBrightness, 0f, 1f);
			registerStandardTree(namespace, "time", Uniforms::getGameTime, 0f, 1f, 1, new MapConfig(List.of(new ConfigData("period", List.of(1.0f)))), false);
			registerStandardTree(namespace, "random", Uniforms::getRandom, 0f, 1f, 1, EmptyConfig.INSTANCE, false);
			registerSingleTree(namespace, "render_type/is_depth_supported", Uniforms::getRenderTypeIsDepthSupported, 0f, 1f);
			registerSingleTree(namespace, "render_type/is_over_ui", Uniforms::getRenderTypeIsOverUi, 0f, 1f);
			registerSingleTree(namespace, "render_type/is_under_ui", Uniforms::getRenderTypeIsUnderUi, 0f, 1f);
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to initialize uniforms: {}", error));
		}
	}
	public static void registerSingleTree(String namespace, String path, Callables.SingleUniformCalculation callable, @Nullable Float min, @Nullable Float max) {
		registerStandardTree(namespace, path, callable.convert(), min, max, 1, null, false);
	}
	public static void registerRangedTree(String namespace, String path, Callables.UniformCalculation callable, Callables.UniformCalculation min, Callables.UniformCalculation max, int length, @Nullable UniformConfig uniformConfig, boolean loop) {
		RootUniform uniform = new RootUniform(path, callable, length, min, max, uniformConfig);
		if (!uniform.useConfig) {
			addStandardChildren(uniform, length, loop);
		} else {
			addElementChildren(uniform, length);
		}
		registerTree(namespace, uniform, null);
	}


	public static void registerStandardTree(String namespace, String path, Callables.UniformCalculation callable, @Nullable Float min, @Nullable Float max, int length, @Nullable UniformConfig uniformConfig, boolean loop) {
		RootUniform uniform = new RootUniform(path, callable, length, UniformValue.fromFloat(min, length), UniformValue.fromFloat(max, length), uniformConfig);
		if (!uniform.useConfig) {
			addStandardChildren(uniform, length, loop);
		} else {
			addElementChildren(uniform, length);
		}
		registerTree(namespace, uniform, null);
	}

	public static void registerTree(String namespace, TreeUniform treeUniform, String path) {
		if (path == null) {
			path = treeUniform.name;
		} else {
			path = path+"_"+treeUniform.name;
		}

		Identifier identifier = Identifier.of(namespace, path);
		treeUniform.onRegister(identifier);
		Events.ShaderUniform.register(identifier, treeUniform);
		for (TreeUniform child : treeUniform.children) {
			registerTree(namespace, child, path);
		}
	}
	@SuppressWarnings("UnusedReturnValue")
    public static TreeUniform addStandardChildren(TreeUniform treeUniform, int length, boolean loop) {
		addElementChildren(
				treeUniform.addChildren(
						addElementChildren(new DeltaUniform(loop), length),
						addElementChildren(new PrevUniform(), length),
						addElementChildren(new SmoothUniform(loop).addChildren(
								addElementChildren(new DeltaUniform(loop), length),
								addElementChildren(new PrevUniform(), length)),
								length
						)
				),
				length
		);
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
	public static float getPanoramaAlpha(ShaderTime shaderTime) {
		return ClientData.minecraft.currentScreen instanceof TitleScreen ? (((LuminanceTitleScreen)ClientData.minecraft.currentScreen).luminance$getBackgroundAlpha()) : 1.0F;
	}
	public static float getHudHidden(ShaderTime shaderTime) {
		return ClientData.minecraft.options != null ? (ClientData.minecraft.options.hudHidden ? 1.0F : 0.0F) : 0.0F;
	}
	public static float getIsInGui(ShaderTime shaderTime) {
		return ClientData.minecraft.currentScreen != null ? 1.0F : 0.0F;
	}
	public static float getViewDistance(ShaderTime shaderTime) {
		return ClientData.minecraft.options != null ? ClientData.minecraft.options.getViewDistance().getValue() : 12.0F;
	}
	public static float getFov(ShaderTime shaderTime) {
		return Accessors.getGameRenderer() != null ? (Accessors.getGameRenderer().invokeGetFov(ClientData.minecraft.gameRenderer.getCamera(), shaderTime.getTickProgress(), true)) : (ClientData.minecraft.options != null ? MinecraftClient.getInstance().options.getFov().getValue() : 70f);
	}
	public static void getGraphicsMode(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		// TODO: find equivalent
		//  it seems its been changed to getPreset, with the actual options split out?
		//  also this should probably be using a SingleTree instead of a StandardTree?

		//uniformValue.set(0, ClientData.minecraft.options.getGraphicsMode().getValue().getId());
	}
	public static float getFps(ShaderTime shaderTime) {
		return ClientData.minecraft.getCurrentFps();
	}
	public static void getGameTime(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		float period = config.getNumber("period", 0).orElse(1.0).floatValue();
		uniformValue.set(0, shaderTime.getModuloTime(period)/period);
	}
	public static void getEye(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(ClientData.minecraft.player.getCameraPosVec(shaderTime.getTickProgress()));
		} else {
			uniformValue.set(new Vec3d(0, 66, 0));
		}
	}
	public static void getEyeFract(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(fract(ClientData.minecraft.player.getCameraPosVec(shaderTime.getTickProgress())));
		} else {
			uniformValue.set(new Vec3d(0, 66, 0));
		}
	}
	public static void getPos(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(ClientData.minecraft.player.getEntityPos());
		} else {
			uniformValue.set(new Vec3d(0, 64, 0));
		}
	}
	public static void getPosFract(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(fract(ClientData.minecraft.player.getEntityPos()));
		} else {
			uniformValue.set(new Vec3d(0, 0, 0));
		}
	}
	public static void getCamera(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(((GameRendererAccessor)ClientData.minecraft.gameRenderer).getCamera().getCameraPos());
		} else {
			uniformValue.set(new Vec3d(0, 64, 0));
		}
	}
	public static void getCameraFract(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(fract(((GameRendererAccessor)ClientData.minecraft.gameRenderer).getCamera().getCameraPos()));
		} else {
			uniformValue.set(new Vec3d(0, 0, 0));
		}
	}
	private static Vec3d fract(Vec3d pos) {
		return new Vec3d(MathHelper.fractionalPart(pos.x), MathHelper.fractionalPart(pos.y), MathHelper.fractionalPart(pos.z));
	}

	public static void getClippingPlanes(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, 0.05f);
		uniformValue.set(1, ClientData.minecraft.gameRenderer.getFarPlaneDistance());
	}

	public static float getPitch(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getPitch(shaderTime.getTickProgress()) % 360.0F : 0.0F;
	}
	public static void getYaw(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		if (ClientData.minecraft.player != null) {
			uniformValue.set(0, MathHelper.floorMod(ClientData.minecraft.player.getYaw(shaderTime.getTickProgress())+180f,360.0F)-180f);
		} else {
			uniformValue.set(0, 0);
		}
	}
	public static void getCurrentHealth(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.getHealth() : 20.0F);
	}
	public static float getMaxHealth(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxHealth() : 20.0F;
	}
	public static void getCurrentAbsorption(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.getAbsorptionAmount() : 0.0F);
	}
	public static float getMaxAbsorption(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAbsorption() : 0.0F;
	}
	public static void getCurrentHurtTime(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.hurtTime : 0.0F);
	}
	public static float getMaxHurtTime(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.maxHurtTime : 10.0F;
	}
	public static void getCurrentAir(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.getAir() : 300.0F);
	}
	public static float getMaxAir(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAir() : 300.0F;
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
	public static void getIsInBiome(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, getIsInBiome(config) ? 0 : 1);
	}
	private static boolean getIsInBiome(UniformConfig config) {
		List<Object> objects = config.getObjects("biome");
		if (ClientData.minecraft.world != null && ClientData.minecraft.player != null && objects != null && !objects.isEmpty() && objects.getFirst() instanceof String id) return Identifier.of(ClientData.minecraft.world.getBiome(ClientData.minecraft.player.getBlockPos()).getIdAsString()).equals(Identifier.of(id));
		return false;
	}
	public static void getEffectDuration(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		StatusEffectInstance instance = getEffect(config);
		uniformValue.set(0, instance == null ? 0 : instance.getDuration());
	}
	public static void getEffectAmplifier(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		StatusEffectInstance instance = getEffect(config);
		uniformValue.set(0, instance == null ? 0 : instance.getAmplifier());
	}
	private static @Nullable StatusEffectInstance getEffect(UniformConfig config) {
		List<Object> objects = config.getObjects("effect");
		if (ClientData.minecraft.player != null && objects != null && !objects.isEmpty() && objects.getFirst() instanceof String id) {
			Optional<RegistryEntry.Reference<StatusEffect>> entry = Registries.STATUS_EFFECT.getEntry(Identifier.of(id));
			if (entry.isPresent()) {
				return ClientData.minecraft.player.getStatusEffect(entry.get());
			}
		}
		return null;
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
		if (LuminanceConfig.config.showAlphaLevelOverlay.value()) MessageOverlay.setOverlay(Translation.getTranslation(Data.getVersion().getID(), "alpha_level", new Object[]{getRawAlpha() + "%"}, new Formatting[]{Formatting.GOLD}));
	}
	public static boolean updatingAlpha = false;
	public static boolean updatingAlpha() {
		if (Keybindings.adjustAlpha != null) {
			boolean value = Keybindings.adjustAlpha.isPressed();
			if (value) {
				if (!updatingAlpha) {
					prevAlpha = getRawAlpha();
				}
				updatingAlpha = true;
			}
			return value;
		}
		return false;
	}
	public static float getPerspective(ShaderTime shaderTime) {
		if (ClientData.minecraft.options != null) {
			Perspective perspective = ClientData.minecraft.options.getPerspective();
			return perspective.equals(Perspective.THIRD_PERSON_FRONT) ? 3.0F : (perspective.equals(Perspective.THIRD_PERSON_BACK) ? 2.0F : (perspective.equals(Perspective.FIRST_PERSON) ? 1.0F : 0.0F));
		}
		return 0.0F;
	}
	public static float getSelectedSlot(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getInventory().getSelectedSlot() : 0.0F;
	}
	public static float getScore(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getScore() : 0.0F;
	}
	public static float getVelocity(ShaderTime shaderTime) {
		if (ClientData.minecraft.player != null) {
			//should also be able to do: ClientData.minecraft.player.getVelocity().length();
			float x = (float) (ClientData.minecraft.player.getX() - ClientData.minecraft.player.lastX);
			float y = (float) (ClientData.minecraft.player.getY() - ClientData.minecraft.player.lastY);
			float z = (float) (ClientData.minecraft.player.getZ() - ClientData.minecraft.player.lastZ);
			return (float) Math.sqrt(x * x + y * y + z * z);
		}
		return 0.0F;
	}
	public static float getSkyAngle(ShaderTime shaderTime) {
		// TODO: sky angle seems to have been split into sun, moon, star angles.
		// ((WorldRendererAccessor)ClientData.minecraft.worldRenderer).getWorldRenderState().skyRenderState.sunAngle;
		// ((WorldRendererAccessor)ClientData.minecraft.worldRenderer).getWorldRenderState().skyRenderState.moonAngle;
		// ((WorldRendererAccessor)ClientData.minecraft.worldRenderer).getWorldRenderState().skyRenderState.starAngle;
		return 0f;
	}
	public static float getSunAngle(ShaderTime shaderTime) {
		float skyAngle = getSkyAngle(shaderTime);
		return skyAngle < 0.75F ? skyAngle + 0.25F : skyAngle - 0.75F;
	}
	public static float getIsDay(ShaderTime shaderTime) {
		return (getSunAngle(shaderTime) <= 0.5) ? 1.0F : 0.0F;
	}

	public static float getStarBrightness(ShaderTime shaderTime) {
		return ((WorldRendererAccessor)ClientData.minecraft.worldRenderer).getWorldRenderState().skyRenderState.starBrightness;
	}

	public static void getRandom(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, Accessors.getGameRenderer().getRandom().nextFloat());
	}

	public static float getRenderTypeIsDepthSupported(ShaderTime shaderTime) {
		return ShaderTime.currentRenderType.isDepthSupported() ? 1.0F : 0.0F;
	}

	public static float getRenderTypeIsOverUi(ShaderTime shaderTime) {
		return ShaderTime.currentRenderType.isOverUi() ? 1.0F : 0.0F;
	}

	public static float getRenderTypeIsUnderUi(ShaderTime shaderTime) {
		return ShaderTime.currentRenderType.isUnderUi() ? 1.0F : 0.0F;
	}

	public static void getZero(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue) {
		uniformValue.set(0, 0F);
	}
}
