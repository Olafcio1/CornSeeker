package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.ServerSeeker;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public static MinecraftClient getInstance() {
        return null;
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void MinecraftClient(CallbackInfo ci) {
        ServerSeeker.mc = getInstance();
    }
}
