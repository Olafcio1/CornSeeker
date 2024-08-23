package de.damcraft.serverseeker.ssapi;

import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Objects;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public final class Servers {
    public static ArrayList<Server> parseJSON(String resp) {
        var raw = gson.fromJson(resp, ArrayList.class);

        var servers = new ArrayList<Server>();
        for (var elr : raw) {
            var el = (LinkedTreeMap) elr;
            var server = new Server();
            server.ip = (String) el.get("ip");
            server.cracked = (Boolean) el.get("cracked");
            server._id = (String) el.get("_id");
            server.enforcesSecureChat = (Boolean) el.get("enforcesSecureChat");
            server.hasFavicon = (Boolean) el.get("hasFavicon");
            server.hasForgeData = (Boolean) el.get("hasForgeData");
            server.lastSeen = (Double) el.get("lastSeen");
            server.org = (String) el.get("org");
            server.port = (Double) el.get("port");
            server.version = new Server.Version(
                Objects.requireNonNullElse((String) el.get("version.name"), ""),
                (Double) el.get("version.protocol")
            );
            server.description = new Server.Description(
                Objects.requireNonNullElse((String) el.get("description.text"), "")
            );
            var hraw = (JsonArray) el.get("players.history");
            var history = new ArrayList<Server.PlayerList.HistoryState>();
            if (hraw != null) {
                for (var e : hraw) {
                    var ep = e.getAsJsonObject();
                    history.add(new Server.PlayerList.HistoryState(ep.get("online").getAsInt(), ep.get("date").getAsInt()));
                }
            }
            var praw = (JsonArray) el.get("players.sample");
            var players = new ArrayList<Server.PlayerList.SamplePlayer>();
            if (praw != null) {
                for (var e : praw) {
                    var ep = e.getAsJsonObject();
                    players.add(new Server.PlayerList.SamplePlayer(ep.get("id").getAsString(), ep.get("lastSeen").getAsInt(), ep.get("name").getAsString()));
                }
            }
            server.players = new Server.PlayerList(
                (Double) el.get("players.max"),
                (Double) el.get("players.online"),
                history,
                players
            );
            server.geo = new Server.Geolocation(
                Objects.requireNonNullElse((String) el.get("geo.city"), ""),
                Objects.requireNonNullElse((String) el.get("geo.country"), ""),
                (Number) el.get("lat"),
                (Number) el.get("lon")
            );
            servers.add(server);
        }

        return servers;
    }
}
