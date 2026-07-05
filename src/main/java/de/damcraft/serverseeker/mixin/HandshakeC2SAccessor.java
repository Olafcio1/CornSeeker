package de.damcraft.serverseeker.mixin;

import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientIntentionPacket.class)
public interface HandshakeC2SAccessor {
    @Mutable
    @Accessor("hostName")
    void setAddress(String address);

    @Accessor("intention")
    ClientIntent getNetworkState();

    @Accessor("hostName")
    String getAddress();
}
