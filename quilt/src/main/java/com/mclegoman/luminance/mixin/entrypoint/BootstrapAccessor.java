/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.entrypoint;

import net.minecraft.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bootstrap.class)
public interface BootstrapAccessor {
	@Invoker("setOutputStreams")
	static void luminance$invokeSetOutputStreams() {
		throw new IllegalStateException("Failed to invoke setOutputStreams!");
	}
}
