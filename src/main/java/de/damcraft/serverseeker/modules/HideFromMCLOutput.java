package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class HideFromMCLOutput extends Module {
    public HideFromMCLOutput() {
        super(ServerSeeker.C.MAIN, "hide-from-mcl-output", "Hides all messages you send from the MCL output window of other players (MCL-21590).");
    }

    @EventHandler
    public void onSendMessage(SendMessageEvent event) {
        event.message = "]]>" + event.message;
    }
}
