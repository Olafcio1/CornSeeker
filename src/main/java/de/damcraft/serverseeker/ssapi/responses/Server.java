package de.damcraft.serverseeker.ssapi.responses;

import java.util.ArrayList;

public class Server {
    public static class PlayerList {
        public static class HistoryState {
            public Integer online;
            public int date;
        }

        public static class SamplePlayer {
            public String id;
            public int lastSeen;
            public String name;
        }

        public Integer max;
        public Integer online;
        public ArrayList<HistoryState> history;
        public ArrayList<SamplePlayer> players;
    }

    public static class Description {
        public String text;
    }

    public static class Geolocation {
        public String city;
        public String country;
        public Number lat;
        public Number lon;
    }

    public static class Version {
        public String name;
        public Integer protocol;
    }

    public Boolean cracked;
    public Description description;
    public Boolean enforcesSecureChat;
    public Geolocation geo;
    public Boolean hasFavicon;
    public Boolean hasForgeData;
    public String ip;
    public Integer lastSeen;
    public String org;
    public PlayerList players;
    public Integer port;
    public Version version;
    public String _id;
}
