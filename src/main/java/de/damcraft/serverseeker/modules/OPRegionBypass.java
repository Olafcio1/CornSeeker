package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class OPRegionBypass extends Module {
    public OPRegionBypass() {
        super(ServerSeeker.C.MAIN, "op-region-bypass", "Uses /setblock and /data on plots to bypass the plot protection. Requires OP.");
    }

    @EventHandler
    public void onStartBreakingBlock(StartBreakingBlockEvent e) {
        e.cancel();
        var info = e.blockPos;
        int x = info.getX(),
            y = info.getY(),
            z = info.getZ();
//            ServerSeeker.mc.player.sendMessage(Text.of("seq:" + ev.getSequence()));
        ServerSeeker.mc.player.networkHandler.sendCommand("setblock " + x + " " + y + " " + z + " air");
    }
}
