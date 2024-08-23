package de.damcraft.serverseeker.ssapi.responses;

import java.util.ArrayList;

public class Server {
    public record PlayerList(Double max, Double online, ArrayList<HistoryState> history, ArrayList<SamplePlayer> players) {
        public record HistoryState(Integer online, Integer date) {}
        public record SamplePlayer(String id, Integer lastSeen, String name) {}
    }

    public record Description(String text) {}

    public record Geolocation(String city, String country, Number lat, Number lon) {}

    public record Version(String name, Double protocol) {}

    public Boolean cracked;
    public Description description;
    public Boolean enforcesSecureChat;
    public Geolocation geo;
    public Boolean hasFavicon;
    public Boolean hasForgeData;
    public String ip;
    public Double lastSeen;
    public String org;
    public PlayerList players;
    public Double port;
    public Version version;
    public String _id;
}
