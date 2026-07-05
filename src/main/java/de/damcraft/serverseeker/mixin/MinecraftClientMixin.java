package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.ServerSeeker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public static Minecraft getInstance() {
        return null;
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void MinecraftClient(CallbackInfo ci) {
        ServerSeeker.mc = getInstance();
    }
}
