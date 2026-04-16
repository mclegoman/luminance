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
import com.mclegoman.luminance.client.shaders.uniforms.UniformVector;
import com.mclegoman.luminance.client.shaders.uniforms.children.DeltaUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.ElementUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.PrevUniform;
import com.mclegoman.luminance.client.shaders.uniforms.children.SmoothUniform;
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
import com.mclegoman.luminance.mixin.client.shaders.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.CameraType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.MoonPhase;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		shaderTime.update(((DynamicRenderTickCounterAccessor)ClientData.minecraft.getDeltaTracker()).getRawTickProgress());
		Events.ShaderUniform.registry.forEach((id, uniform) -> uniform.update(shaderTime));
	}

	public static void init() {
		try {
			String namespace = Data.getVersion().getID();
			// TODO: crosshair target (i swear it used to exist?)

			registerSingleValueTree(namespace, "panorama_alpha", Uniforms::getPanoramaAlpha, 0f, 1f);
			registerSingleValueTree(namespace, "hud_hidden", Uniforms::getHudHidden, 0f, 1f);
			registerSingleValueTree(namespace, "is_in_gui", Uniforms::getIsInGui, 0f, 1f);
			registerSingleValueTree(namespace, "view_distance", Uniforms::getViewDistance, 2f, null);
			registerSingleValueTree(namespace, "fov", Uniforms::getFov, 0f, 360f);
			registerSingleValueTree(namespace, "fps", Uniforms::getFps, 0f, null);
			registerFullTree(namespace, "graphics_mode", Uniforms::getGraphicsMode, 0f, 2f, 1, EmptyConfig.INSTANCE, false);
			registerFullTree(namespace, "eye", Uniforms::getEye, null, null, 3, null, false);
			registerFullTree(namespace, "eye_fract", Uniforms::getEyeFract, 0f, 1f, 3, null, true);
			registerFullTree(namespace, "pos", Uniforms::getPos, null, null, 3, null, false);
			registerFullTree(namespace, "pos_fract", Uniforms::getPosFract, 0f, 1f, 3, null,  true);
			registerFullTree(namespace, "cam", Uniforms::getCamera, null, null, 3, EmptyConfig.INSTANCE, false);
			registerFullTree(namespace, "cam_fract", Uniforms::getCameraFract, 0f, 1f, 3, EmptyConfig.INSTANCE,  true);
			registerSingleValueTree(namespace, "pitch", Uniforms::getPitch, -90f, 90f);
			registerFullTree(namespace, "yaw", Uniforms::getYaw, -180f, 180f, 1, null, true);
			registerFullTree(namespace, "clipping", Uniforms::getClippingPlanes, 0f, null, 2, null, false);
			registerSingleValueTree(namespace, "velocity", Uniforms::getVelocity, 0f, null);
			registerDynamicallyRangedTree(namespace, "current_health", Uniforms::getCurrentHealth, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxHealth(b)), 1, null, false);
			registerSingleValueTree(namespace, "max_health", Uniforms::getMaxHealth, 0f, null);
			registerDynamicallyRangedTree(namespace, "current_absorption", Uniforms::getCurrentAbsorption, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxAbsorption(b)), 1, null, false);
			registerSingleValueTree(namespace, "max_absorption", Uniforms::getMaxAbsorption, 0f, null);
			registerDynamicallyRangedTree(namespace, "current_hurt_time", Uniforms::getCurrentHurtTime, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxHurtTime(b)), 1, null, false);
			registerSingleValueTree(namespace, "max_hurt_time", Uniforms::getMaxHurtTime, 0f, null);
			registerDynamicallyRangedTree(namespace, "current_air", Uniforms::getCurrentAir, Uniforms::getZero, (a, b, c) -> c.set(0, getMaxAir(b)), 1, null, false);
			registerSingleValueTree(namespace, "max_air", Uniforms::getMaxAir, 0f, null);
			registerSingleValueTree(namespace, "is_alive", Uniforms::getIsAlive, 0f, 1f);
			registerSingleValueTree(namespace, "is_dead", Uniforms::getIsDead, 0f, 1f);
			registerSingleValueTree(namespace, "is_sprinting", Uniforms::getIsSprinting, 0f, 1f);
			registerSingleValueTree(namespace, "is_swimming", Uniforms::getIsSwimming, 0f, 1f);
			registerSingleValueTree(namespace, "is_sneaking", Uniforms::getIsSneaking, 0f, 1f);
			registerSingleValueTree(namespace, "is_crawling", Uniforms::getIsCrawling, 0f, 1f);
			registerSingleValueTree(namespace, "is_invisible", Uniforms::getIsInvisible, 0f, 1f);
			registerSingleValueTree(namespace, "is_withered", (shaderTime) -> Uniforms.getHasEffect(MobEffects.WITHER), 0f, 1f);
			registerSingleValueTree(namespace, "is_poisoned", (shaderTime) -> Uniforms.getHasEffect(MobEffects.POISON), 0f, 1f);
			registerFullTree(namespace, "is_in_biome", Uniforms::getIsInBiome, 0f, 1f, 1, new MapConfig(Map.of("biome", List.of("minecraft:plains"))), false);
			registerFullTree(namespace, "effect_duration", Uniforms::getEffectDuration, null, null, 1, new MapConfig(Map.of("effect", List.of("minecraft:speed"))), false);
			registerFullTree(namespace, "effect_amplifier", Uniforms::getEffectAmplifier, 0f, 255f, 1, new MapConfig(Map.of("effect", List.of("minecraft:speed"))), false);
			registerSingleValueTree(namespace, "is_burning", Uniforms::getIsBurning, 0f, 1f);
			registerSingleValueTree(namespace, "is_on_ground", Uniforms::getIsOnGround, 0f, 1f);
			registerSingleValueTree(namespace, "is_on_ladder", Uniforms::getIsOnLadder, 0f, 1f);
			registerSingleValueTree(namespace, "is_riding", Uniforms::getIsRiding, 0f, 1f);
			registerSingleValueTree(namespace, "has_passengers", Uniforms::getHasPassengers, 0f, 1f);
			registerSingleValueTree(namespace, "biome_temperature", Uniforms::getBiomeTemperature, 0f, 1f);
			registerSingleValueTree(namespace, "alpha", Uniforms::getAlpha, 0f, 1f);
			registerSingleValueTree(namespace, "perspective", Uniforms::getPerspective, 0f, 3f);
			registerSingleValueTree(namespace, "selected_slot", Uniforms::getSelectedSlot, 0f, 8f);
			registerSingleValueTree(namespace, "score", Uniforms::getScore, 0f, null);
			registerSingleValueTree(namespace, "is_sky_dark", Uniforms::getSkyDark, 0f, 1f);
			registerSingleValueTree(namespace, "sun_angle", Uniforms::getSunAngle, 0f, Mth.TWO_PI);
			registerSingleValueTree(namespace, "moon_angle", Uniforms::getMoonAngle, 0f, Mth.TWO_PI);
			registerSingleValueTree(namespace, "star_angle", Uniforms::getStarAngle, 0f, Mth.TWO_PI);
			registerSingleValueTree(namespace, "rain_gradient", Uniforms::getRainGradient, 0f, 1f);
			registerSingleValueTree(namespace, "star_brightness", Uniforms::getStarBrightness, 0f, 1f);
			registerSingleValueTree(namespace, "sunrise_and_sunset_color", Uniforms::getSunriseAndSunsetColor, null, null);
			registerSingleValueTree(namespace, "moon_phase", Uniforms::getMoonPhase, 0f , (float) MoonPhase.COUNT);
			registerSingleValueTree(namespace, "sky_color", Uniforms::getSkyColor, null, null);
			registerSingleValueTree(namespace, "end_flash_intensity", Uniforms::getEndFlashIntensity, 0f, 1f);
			registerSingleValueTree(namespace, "end_flash_pitch", Uniforms::getEndFlashPitch, -90f, 90f);
			registerSingleValueTree(namespace, "end_flash_yaw", Uniforms::getEndFlashYaw, -180f, 180f);
			registerSingleValueTree(namespace, "is_day", Uniforms::getIsDay, 0f, 1f);
			registerFullTree(namespace, "time", Uniforms::getGameTime, 0f, 1f, 1, new MapConfig(Map.of("period", List.of(1.0))), false);
			registerFullTree(namespace, "random", Uniforms::getRandom, 0f, 1f, 1, EmptyConfig.INSTANCE, false);
			registerFullTree(namespace, "render_location", Uniforms::getRenderLocation, 0f, 1f, 3, EmptyConfig.INSTANCE, false);
			registerSingleValueTree(namespace, "gamemode_has_health", Uniforms::getGameModeHasHealth, 0f, 1f);
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to initialize uniforms: {}", error);
		}

		Events.OnMouseScroll.register(Data.idOf("update_alpha"), (long windowHandle, double horizontal, double vertical, Vector2i scroll) -> {
			if (Uniforms.updatingAlpha()) {
                if (ClientData.minecraft.player != null) {
					int scrollAmount = scroll.y == 0 ? -scroll.x : scroll.y;
					Uniforms.adjustAlpha(scrollAmount);
					return true;
				}
			}
			return false;
		});

		Events.OnMouseButton.register(Data.idOf("reset_alpha"), (windowHandle, mouseButtonInfo, action) -> {
			if (Uniforms.updatingAlpha()) {
				if (mouseButtonInfo.button() == 2) {
					Uniforms.resetAlpha();
					return true;
				}
			}
			return false;
		});
	}

	public static void registerSingleValueTree(String namespace, String path, Callables.SingleValueUniformCalculation callable, @Nullable Float min, @Nullable Float max) {
		registerFullTree(namespace, path, callable.convert(), min, max, 1, null, false);
	}

	public static void registerDynamicallyRangedTree(String namespace, String path, Callables.UniformCalculation callable, Callables.UniformCalculation min, Callables.UniformCalculation max, int length, @Nullable UniformConfig uniformConfig, boolean loop) {
		RootUniform uniform = new RootUniform(path, callable, length, min, max, uniformConfig);
		if (!uniform.useConfig) {
			addCalculationChildren(uniform, length, loop);
		} else {
			addElementChildren(uniform, length);
		}
		registerTree(namespace, uniform, null);
	}

	public static void registerFullTree(String namespace, String path, Callables.UniformCalculation callable, @Nullable Float min, @Nullable Float max, int length, @Nullable UniformConfig uniformConfig, boolean loop) {
		RootUniform uniform = new RootUniform(path, callable, length, UniformVector.fromFloat(min, length), UniformVector.fromFloat(max, length), uniformConfig);
		if (!uniform.useConfig) {
			addCalculationChildren(uniform, length, loop);
		} else {
			addElementChildren(uniform, length);
		}
		registerTree(namespace, uniform, null);
	}

	public static void registerTree(String namespace, TreeUniform treeUniform, String path) {
		if (path == null) {
			path = treeUniform.name;
		} else {
			path = path+"/"+treeUniform.name;
		}

		Identifier identifier = Identifier.fromNamespaceAndPath(namespace, path);
		treeUniform.onRegister(identifier);
		Events.ShaderUniform.register(identifier, treeUniform);
		for (TreeUniform child : treeUniform.children) {
			registerTree(namespace, child, path);
		}
	}

	@SuppressWarnings("UnusedReturnValue")
    public static TreeUniform addCalculationChildren(TreeUniform treeUniform, int length, boolean loop) {
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
		return ClientData.minecraft.screen instanceof TitleScreen ? (((LuminanceTitleScreen)ClientData.minecraft.screen).luminance$getBackgroundAlpha()) : 1.0F;
	}

	public static float getHudHidden(ShaderTime shaderTime) {
		return ClientData.minecraft.options.hideGui ? 1.0F : 0.0F;
	}

	public static float getIsInGui(ShaderTime shaderTime) {
		return ClientData.minecraft.screen != null ? 1.0F : 0.0F;
	}

	public static float getViewDistance(ShaderTime shaderTime) {
		return ClientData.minecraft.options.renderDistance().get();
	}

	public static float getFov(ShaderTime shaderTime) {
		return Accessors.getGameRenderer() != null ? Accessors.getGameRenderer().invokeGetFov(ClientData.minecraft.gameRenderer.getMainCamera(), shaderTime.getTickProgress(), true) : Minecraft.getInstance().options.fov().get();
	}

	public static void getGraphicsMode(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		// TODO: find equivalent
		//  it seems its been changed to getPreset, with the actual options split out?
		//  also this should probably be using a SingleTree instead of a StandardTree?

		//uniformValue.set(0, ClientData.minecraft.options.getGraphicsMode().getValue().getId());
	}

	public static float getFps(ShaderTime shaderTime) {
		return ClientData.minecraft.getFps();
	}

	public static void getGameTime(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		float period = config.getNumber("period", 0).orElse(1.0).floatValue();
		uniformVector.set(0, shaderTime.getModuloTime(period)/period);
	}

	public static void getEye(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(ClientData.minecraft.player.getEyePosition(shaderTime.getTickProgress()));
		} else {
			uniformVector.set(new Vec3(0, 66, 0));
		}
	}

	public static void getEyeFract(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(fract(ClientData.minecraft.player.getEyePosition(shaderTime.getTickProgress())));
		} else {
			uniformVector.set(new Vec3(0, 66, 0));
		}
	}

	public static void getPos(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(ClientData.minecraft.player.position());
		} else {
			uniformVector.set(new Vec3(0, 64, 0));
		}
	}

	public static void getPosFract(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(fract(ClientData.minecraft.player.position()));
		} else {
			uniformVector.set(new Vec3(0, 0, 0));
		}
	}

	public static void getCamera(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(((GameRendererAccessor)ClientData.minecraft.gameRenderer).getMainCamera().position());
		} else {
			uniformVector.set(new Vec3(0, 64, 0));
		}
	}

	public static void getCameraFract(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(fract(((GameRendererAccessor)ClientData.minecraft.gameRenderer).getMainCamera().position()));
		} else {
			uniformVector.set(new Vec3(0, 0, 0));
		}
	}

	private static Vec3 fract(Vec3 pos) {
		return new Vec3(Mth.frac(pos.x), Mth.frac(pos.y), Mth.frac(pos.z));
	}

	public static void getClippingPlanes(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, 0.05f);
		uniformVector.set(1, ClientData.minecraft.gameRenderer.getDepthFar());
	}

	public static float getPitch(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getViewXRot(shaderTime.getTickProgress()) % 360.0F : 0.0F;
	}

	public static void getYaw(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		if (ClientData.minecraft.player != null) {
			uniformVector.set(0, Mth.positiveModulo(ClientData.minecraft.player.getViewYRot(shaderTime.getTickProgress())+180f,360.0F)-180f);
		} else {
			uniformVector.set(0, 0);
		}
	}

	public static void getCurrentHealth(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.getHealth() : 20.0F);
	}

	public static float getMaxHealth(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxHealth() : 20.0F;
	}

	public static void getCurrentAbsorption(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.getAbsorptionAmount() : 0.0F);
	}

	public static float getMaxAbsorption(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAbsorption() : 0.0F;
	}

	public static void getCurrentHurtTime(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.hurtTime : 0.0F);
	}

	public static float getMaxHurtTime(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.hurtDuration : 10.0F;
	}

	public static void getCurrentAir(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, ClientData.minecraft.player != null ? ClientData.minecraft.player.getAirSupply() : 300.0F);
	}

	public static float getMaxAir(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? ClientData.minecraft.player.getMaxAirSupply() : 300.0F;
	}

	public static float getIsAlive(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isAlive() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsDead(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isDeadOrDying() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsSprinting(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSprinting() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsSwimming(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSwimming() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsSneaking(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isShiftKeyDown() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsCrawling(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isVisuallyCrawling() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsInvisible(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isInvisible() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getHasEffect(Holder<MobEffect> statusEffect) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.hasEffect(statusEffect) ? 1.0F : 0.0F) : 0.0F;
	}

	public static void getIsInBiome(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, getIsInBiome(config) ? 0 : 1);
	}

	private static boolean getIsInBiome(UniformConfig config) {
		List<Object> objects = config.getObjects("biome");
		if (ClientData.minecraft.level != null && ClientData.minecraft.player != null && objects != null && !objects.isEmpty() && objects.getFirst() instanceof String id) return Identifier.parse(ClientData.minecraft.level.getBiome(ClientData.minecraft.player.blockPosition()).getRegisteredName()).equals(Identifier.parse(id));
		return false;
	}

	public static void getEffectDuration(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		MobEffectInstance instance = getEffect(config);
		uniformVector.set(0, instance == null ? 0 : instance.getDuration());
	}

	public static void getEffectAmplifier(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		MobEffectInstance instance = getEffect(config);
		uniformVector.set(0, instance == null ? 0 : instance.getAmplifier());
	}

	private static @Nullable MobEffectInstance getEffect(UniformConfig config) {
		List<Object> objects = config.getObjects("effect");
		if (ClientData.minecraft.player != null && objects != null && !objects.isEmpty() && objects.getFirst() instanceof String id) {
			Optional<Holder.Reference<MobEffect>> entry = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(id));
			if (entry.isPresent()) {
				return ClientData.minecraft.player.getEffect(entry.get());
			}
		}
		return null;
	}

	public static float getIsBurning(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isOnFire() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getIsOnGround(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.onGround() ? 1.0F : 0.0F) : 1.0F;
	}

	public static float getIsOnLadder(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isSuppressingSlidingDownLadder() ? 1.0F : 0.0F) : 1.0F;
	}

	public static float getIsRiding(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isHandsBusy() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getHasPassengers(ShaderTime shaderTime) {
		return ClientData.minecraft.player != null ? (ClientData.minecraft.player.isVehicle() ? 1.0F : 0.0F) : 0.0F;
	}

	public static float getBiomeTemperature(ShaderTime shaderTime) {
		return ClientData.minecraft.level != null && ClientData.minecraft.player != null ? ClientData.minecraft.level.getBiome(ClientData.minecraft.player.blockPosition()).value().getBaseTemperature() : 1.0F;
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
		if (LuminanceConfig.config.showAlphaLevelOverlay.value()) MessageOverlay.setOverlay(Translation.getTranslation(Data.getVersion().getID(), "alpha_level", new Object[]{getRawAlpha() + "%"}, new ChatFormatting[]{ChatFormatting.GOLD}));
	}

	public static boolean updatingAlpha = false;
	public static boolean updatingAlpha() {
		if (Keybindings.adjustAlpha != null) {
			boolean value = Keybindings.adjustAlpha.isDown();
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
        CameraType perspective = ClientData.minecraft.options.getCameraType();
        return perspective.equals(CameraType.THIRD_PERSON_FRONT) ? 3.0F : (perspective.equals(CameraType.THIRD_PERSON_BACK) ? 2.0F : (perspective.equals(CameraType.FIRST_PERSON) ? 1.0F : 0.0F));
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
			float x = (float) (ClientData.minecraft.player.getX() - ClientData.minecraft.player.xo);
			float y = (float) (ClientData.minecraft.player.getY() - ClientData.minecraft.player.yo);
			float z = (float) (ClientData.minecraft.player.getZ() - ClientData.minecraft.player.zo);
			return (float) Math.sqrt(x * x + y * y + z * z);
		}
		return 0.0F;
	}

	public static float getSkyDark(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.shouldRenderDarkDisc ? 1.0F : 0.0F;
	}

	public static float getSunAngle(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.sunAngle;
	}

	public static float getMoonAngle(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.moonAngle;
	}

	public static float getStarAngle(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.starAngle;
	}

	public static float getRainGradient(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.rainBrightness;
	}

	public static float getStarBrightness(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.starBrightness;
	}

	public static int getSunriseAndSunsetColor(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.sunriseAndSunsetColor;
	}

	public static float getMoonPhase(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.moonPhase.index();
	}

	public static int getSkyColor(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.skyColor;
	}

	public static float getEndFlashIntensity(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.endFlashIntensity;
	}

	public static float getEndFlashPitch(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.endFlashXAngle;
	}

	public static float getEndFlashYaw(ShaderTime shaderTime) {
		return ((LevelRendererAccessor)ClientData.minecraft.levelRenderer).getLevelRenderState().skyRenderState.endFlashYAngle;
	}

	public static float getIsDay(ShaderTime shaderTime) {
		return (getSunAngle(shaderTime) <= 0.5) ? 1.0F : 0.0F;
	}

	public static void getRandom(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, Accessors.getGameRenderer().getRandom().nextFloat());
	}

	public static void getRenderLocation(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(new Vec2(
				ShaderTime.currentRenderLocation.depthType().ordinal(),
				ShaderTime.currentRenderLocation.uiType().ordinal()
		));
	}

	public static float getGameModeHasHealth(ShaderTime shaderTime) {
		if (ClientData.minecraft.player != null && ClientData.minecraft.player.gameMode() != null) {
			if (Objects.requireNonNull(ClientData.minecraft.player.gameMode()).isSurvival()) return 1.0F;
		}
		return 0.0F;
	}

	public static void getZero(UniformConfig config, ShaderTime shaderTime, UniformVector uniformVector) {
		uniformVector.set(0, 0F);
	}
}
