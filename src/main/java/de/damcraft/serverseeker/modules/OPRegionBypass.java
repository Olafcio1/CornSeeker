package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;

public class OPRegionBypass extends Module {
    public OPRegionBypass() {
        super(ServerSeeker.CATEGORY, "op-region-bypass", "Uses /setblock and /data on plots to bypass the plot protection. Requires OP.");
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (e.packet instanceof PlayerInteractBlockC2SPacket ev) {
            e.cancel();
            var info = ev.getBlockHitResult().getBlockPos();
            int x = info.getX(),
                y = info.getY(),
                z = info.getZ();
            ServerSeeker.mc.player.sendMessage(Text.of("seq:" + ev.getSequence()));
            ServerSeeker.mc.player.networkHandler.sendCommand("/setblock " + x + " " + y + " " + z + " air");
        }
    }
}
