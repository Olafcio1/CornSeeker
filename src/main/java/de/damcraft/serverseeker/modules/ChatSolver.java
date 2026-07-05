package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.utils.MathUtil;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import org.jetbrains.annotations.NotNull;

public class ChatSolver extends Module {
    public ChatSolver() {
        super(ServerSeeker.C.MAIN, "ChatSolver", "Solves chat minigames.");
    }

    public void solve(String message) {
        var step = 0;
        var nickchars = "1234567890qwertyuiopasdfghjklzxvbnm-.+";
        var low = message.toLowerCase();
        var chars = low.split("");
        if (message.startsWith("<") || (!message.contains("chat") && !message.contains("game")))
            return;
//        for (var ch : chars) {
//            if (step == 0) {
//                if (ch == "<")
//                    step = 1;
//                else break;
//            } else if (step == 1) {
//                if (ch == ">" || !nickchars.contains(ch))
//                    return;
//            }
//        }

        ServerSeeker.mc.player.sendSystemMessage(Component.literal("[ChatSolver] Analyzing..."));

        var lines = low.split("\n");
        var found = false;
        for (var l : lines) {
            var t = getTargetString(l.split(""));
            if (t.isEmpty())
                continue;

            if (l.contains("solve") || l.contains("math")) {
                try {
                    var res = MathUtil.eval(t);
                    // Yes math!!
                    ServerSeeker.mc.player.connection.sendChat(String.valueOf(res));
                    found = true;
                    break;
                } catch (Throwable ignored) {
                    // not math :(
                }
            } else if (l.contains("repeat") || l.contains("say")) {
                ServerSeeker.mc.player.connection.sendChat(t);
                found = true;
                break;
            }
        }

        ServerSeeker.mc.player.sendSystemMessage(Component.literal(found ? "[ChatSolver] Game solved." : "[ChatSolver] Game failed."));
    }

    private static @NotNull String getTargetString(String[] chars) {
        var inColor = false;
        var inBrackets = false;
        var t = new StringBuilder();
        var normal = "`1234567890qwertyuiopasdfghjklzxvbnm";
        for (var ch : chars)
            if (inBrackets) {
                if (ch.equals("]") || ch.equals(")") || ch.equals("}") || ch.equals(">"))
                    inBrackets = false;
            } else if (inColor) {
                inColor = false;
            } else if (ch.equals("§")) {
                inColor = true;
            } else if (ch.equals("[") || ch.equals("(") || ch.equals("{") || ch.equals("<")) {
                inBrackets = true;
            } else if (/*ch == "|" || ch == ":" || */!normal.contains(ch)) {
                t.setLength(0);
            } else t.append(ch);
        return t.toString().trim();
    }

    @EventHandler
    public void onReceiveMessage(ReceiveMessageEvent e) {
        if (e.getIndicator() == null || e.getIndicator().logTag() == null)
            solve(e.getMessage().getString());
    }

    private String title;

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.packet instanceof ClientboundSetTitleTextPacket c) {
            title = c.text().getString();
        } else if (e.packet instanceof ClientboundSetSubtitleTextPacket c) {
            if (title != null) {
                solve(title + " " + c.text().getString());
                title = null;
            }
        }
    }
}
