package de.damcraft.serverseeker.mixin;

import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.modules.BungeeSpoofModule;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.damcraft.serverseeker.ServerSeeker.gson;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(ClientIntentionPacket.class)
public abstract class HandshakeC2SMixin {

    @Mutable
    @Shadow
    @Final
    private String hostName;

    @Shadow
    public abstract ClientIntent intendedState();

    @Inject(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/protocol/handshake/ClientIntent;)V", at = @At("RETURN"))
    private void onHandshakeC2SPacket(int i, String string, int j, ClientIntent connectionIntent, CallbackInfo ci) {
        BungeeSpoofModule bungeeSpoofModule = Modules.get().get(BungeeSpoofModule.class);
        if (!bungeeSpoofModule.isActive()) return;
        if (this.intendedState() != ClientIntent.LOGIN) return;
        ServerSeeker.LOG.info("Spoofing bungeecord handshake packet");
        String spoofedUUID = mc.getUser().getProfileId().toString();

        String URL = "https://api.mojang.com/users/profiles/minecraft/" + mc.getUser().getName();

        Http.Request request = Http.get(URL);
        String response = request.sendString();
        if (response != null) {
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            if (jsonObject != null && jsonObject.has("id")) {
                spoofedUUID = jsonObject.get("id").getAsString();
            }
        }

        this.hostName += "\u0000" + bungeeSpoofModule.spoofedAddress.get() + "\u0000" + spoofedUUID;
    }
}
