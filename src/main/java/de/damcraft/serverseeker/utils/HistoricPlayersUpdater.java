package de.damcraft.serverseeker.utils;

import de.damcraft.serverseeker.SmallHttp;
import de.damcraft.serverseeker.hud.HistoricPlayersHud;
import de.damcraft.serverseeker.ssapi.Server;
import de.damcraft.serverseeker.ssapi.Servers;
import de.damcraft.serverseeker.ssapi.requests.ServersRequest;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.damcraft.serverseeker.ServerSeeker.gson;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class HistoricPlayersUpdater {
    @EventHandler
    private static void onGameJoinEvent(GameJoinedEvent ignoredEvent) {
        // Run in a new thread
        new Thread(HistoricPlayersUpdater::update).start();
    }

    public static void update() {
        // If the Hud contains the HistoricPlayersHud, update the players
        List<HistoricPlayersHud> huds = new ArrayList<>();
        for (HudElement hudElement : Hud.get()) {
            if (hudElement instanceof HistoricPlayersHud && hudElement.isActive()) {
                huds.add((HistoricPlayersHud) hudElement);
            }
        }
        if (huds.isEmpty()) return;

        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler == null) return;

        String address = networkHandler.getConnection().getAddress().toString();
        // Split it at "/" and take the second part
        String[] addressParts = address.split("/");
        if (addressParts.length < 2) return;

        String ip = addressParts[1].split(":")[0];
        Integer port = Integer.valueOf(addressParts[1].split(":")[1]);

        var request = new ServersRequest();
        request.setIpSubnet(ip);
        request.setPort(port);

        String jsonResp = SmallHttp.get("https://api.cornbread2100.com/servers" + request.query());

        var resp = Servers.parseJSON(jsonResp);
        Server server = null;
        for (var s : resp) {
            if (Objects.equals(s.ip, ip) && s.port == port.doubleValue()) {
                server = s;
                break;
            }
        }
        if (server == null)
            return;

        for (HistoricPlayersHud hud : huds) {
            hud.players = Objects.requireNonNullElseGet(server.players.samples(), List::of);
            hud.isCracked = server.cracked != null && server.cracked;
        }
    }
}
