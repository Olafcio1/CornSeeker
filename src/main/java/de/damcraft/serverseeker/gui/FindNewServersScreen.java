package de.damcraft.serverseeker.gui;

import com.google.common.net.HostAndPort;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.SmallHttp;
import de.damcraft.serverseeker.country.Country;
import de.damcraft.serverseeker.country.CountrySetting;
import de.damcraft.serverseeker.ssapi.requests.ServersRequest;
import de.damcraft.serverseeker.ssapi.responses.Server;
import de.damcraft.serverseeker.utils.MCVersionUtil;
import de.damcraft.serverseeker.utils.MultiplayerScreenUtil;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class FindNewServersScreen extends WindowScreen {
    public static NbtCompound savedSettings;
    private int timer;
    public WButton findButton;
    private boolean threadHasFinished;
    private String threadError;
    private List<Server> threadServers;

    public enum Cracked {
        Any,
        Yes,
        No;

        public Boolean toBoolOrNull() {
            return switch (this) {
                case Any -> null;
                case Yes -> true;
                case No -> false;
            };
        }
    }

    public enum Version {
        Current,
        Any,
        Protocol,
        VersionString;

        @Override
        public String toString() {
            return switch (this) {
                case Current -> "Current";
                case Any -> "Any";
                case Protocol -> "Protocol";
                case VersionString -> "Version String";
            };
        }
    }

    public enum NumRangeType {
        Any,
        Equals,
        AtLeast,
        AtMost,
        Between;
        @Override
        public String toString() {
            return switch (this) {
                case Any -> "Any";
                case Equals -> "Equal To";
                case AtLeast -> "At Least";
                case AtMost -> "At Most";
                case Between -> "Between";
            };
        }
    }

    // Didn't have a better name
    public enum GeoSearchType {
        None,
        ASN,
        Country
    }

    private final Settings settings = new Settings();
    private final SettingGroup sg = settings.getDefaultGroup();
    WContainer settingsContainer;

    private final Setting<Cracked> crackedSetting = sg.add(new EnumSetting.Builder<Cracked>()
        .name("cracked")
        .description("Whether the server should be cracked or not")
        .defaultValue(Cracked.Any)
        .build()
    );

    private final Setting<NumRangeType> onlinePlayersNumTypeSetting = sg.add(new EnumSetting.Builder<NumRangeType>()
        .name("online-players-range")
        .description("The type of number range for the online players")
        .defaultValue(NumRangeType.Any)
        .build()
    );

    private final Setting<Integer> equalsOnlinePlayersSetting = sg.add(new IntSetting.Builder()
            .name("online-players")
            .description("The amount of online players the server should have")
            .defaultValue(2)
            .min(0)
            .visible(() -> onlinePlayersNumTypeSetting.get().equals(NumRangeType.Equals))
            .noSlider()
            .build()
    );


    private final Setting<Integer> atLeastOnlinePlayersSetting = sg.add(new IntSetting.Builder()
        .name("minimum-online-players")
        .description("The minimum amount of online players the server should have")
        .defaultValue(1)
        .min(0)
        .visible(() -> onlinePlayersNumTypeSetting.get().equals(NumRangeType.AtLeast) || onlinePlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );

    private final Setting<Integer> atMostOnlinePlayersSetting = sg.add(new IntSetting.Builder()
        .name("maximum-online-players")
        .description("The maximum amount of online players the server should have")
        .defaultValue(20)
        .min(0)
        .visible(() -> onlinePlayersNumTypeSetting.get().equals(NumRangeType.AtMost) || onlinePlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );


    private final Setting<NumRangeType> maxPlayersNumTypeSetting = sg.add(new EnumSetting.Builder<NumRangeType>()
        .name("max-players-range")
        .description("The type of number range for the max players")
        .defaultValue(NumRangeType.Any)
        .build()
    );

    private final Setting<Integer> equalsMaxPlayersSetting = sg.add(new IntSetting.Builder()
            .name("max-players")
            .description("The amount of max players the server should have")
            .defaultValue(2)
            .min(0)
            .visible(() -> maxPlayersNumTypeSetting.get().equals(NumRangeType.Equals))
            .noSlider()
            .build()
    );


    private final Setting<Integer> atLeastMaxPlayersSetting = sg.add(new IntSetting.Builder()
        .name("minimum-max-players")
        .description("The minimum amount of max players the server should have")
        .defaultValue(1)
        .min(0)
        .visible(() -> maxPlayersNumTypeSetting.get().equals(NumRangeType.AtLeast) || maxPlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );

    private final Setting<Integer> atMostMaxPlayersSetting = sg.add(new IntSetting.Builder()
        .name("maximum-max-players")
        .description("The maximum amount of max players the server should have")
        .defaultValue(20)
        .min(0)
        .visible(() -> maxPlayersNumTypeSetting.get().equals(NumRangeType.AtMost) || maxPlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );

    private final Setting<String> descriptionSetting = sg.add(new StringSetting.Builder()
        .name("MOTD")
        .description("What the MOTD of the server should contain (empty for any)")
        .defaultValue("")
        .build()
    );

    private final Setting<String> softwareSetting = sg.add(new StringSetting.Builder()
        .name("software")
        .description("The server software the servers should have (regex)")
        .defaultValue("")
        .build()
    );

    private final Setting<Version> versionSetting = sg.add(new EnumSetting.Builder<Version>()
        .name("version")
        .description("The protocol version the servers should have")
        .defaultValue(Version.Current)
        .build()
    );

    private final Setting<Integer> protocolVersionSetting = sg.add(new IntSetting.Builder()
        .name("protocol")
        .description("The protocol version the servers should have")
        .defaultValue(SharedConstants.getProtocolVersion())
        .visible(() -> versionSetting.get() == Version.Protocol)
        .min(0)
        .noSlider()
        .build()
    );

    private final Setting<String> versionStringSetting = sg.add(new StringSetting.Builder()
        .name("version-string")
        .description("The version string (e.g. 1.19.3) of the protocol version the server should have, results may contain different versions that have the same protocol version. Must be at least 1.7.1")
        .defaultValue("1.20.2")
        .visible(() -> versionSetting.get() == Version.VersionString)
        .build()
    );

    private final Setting<Boolean> onlineOnlySetting = sg.add(new BoolSetting.Builder()
        .name("online-only")
        .description("Whether to only show servers that are online")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> sendsPlayerlist = sg.add(new BoolSetting.Builder()
        .name("sends-playerlist")
        .description("Will not give you servers doesn't send the player sample.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> onlyBungeeSpoofable = sg.add(new BoolSetting.Builder()
        .name("only-bungee-spoofable")
        .description("Will only give you servers where you can use BungeeSpoof")
        .defaultValue(false)
        .build()
    );

    private final Setting<GeoSearchType> geoSearchTypeSetting = sg.add(new EnumSetting.Builder<GeoSearchType>()
        .name("geo-search-type")
        .description("Whether to search by ASN or country code")
        .defaultValue(GeoSearchType.Country)
        .build()
    );

    private final Setting<Integer> asnNumberSetting = sg.add(new IntSetting.Builder()
        .name("asn")
        .description("The ASN of the server")
        .defaultValue(24940)
        .noSlider()
        .visible(() -> geoSearchTypeSetting.get() == GeoSearchType.ASN)
        .build()
    );

    private final Setting<Country> countrySetting = sg.add(new CountrySetting.Builder()
        .name("country")
        .description("The country the server should be located in")
        .defaultValue(ServerSeeker.COUNTRY_MAP.get("UN"))
        .visible(() -> geoSearchTypeSetting.get() == GeoSearchType.Country)
        .build()
    );


    MultiplayerScreen multiplayerScreen;


    public FindNewServersScreen(MultiplayerScreen multiplayerScreen) {
        super(GuiThemes.get(), "Find new servers");
        this.multiplayerScreen = multiplayerScreen;
    }

    @Override
    public void initWidgets() {
        loadSettings();
        onClosed(this::saveSettings);
        settingsContainer = add(theme.verticalList()).widget();
        settingsContainer.add(theme.settings(settings));
        add(theme.button("Reset all")).expandX().widget().action = this::resetSettings;
        findButton = add(theme.button("Find")).expandX().widget();
        findButton.action = () -> {
            ServersRequest request = new ServersRequest();

            switch (onlinePlayersNumTypeSetting.get()) {
                // [n, "inf"]
                case AtLeast -> request.setOnlinePlayers(atLeastOnlinePlayersSetting.get(), -1);

                // [0, n]
                case AtMost -> request.setOnlinePlayers(0, atMostOnlinePlayersSetting.get());

                // [min, max]
                case Between -> request.setOnlinePlayers(atLeastOnlinePlayersSetting.get(), atMostOnlinePlayersSetting.get());

                // [n, n]
                case Equals -> request.setOnlinePlayers(equalsOnlinePlayersSetting.get());
            }

            switch (maxPlayersNumTypeSetting.get()) {
                // [n, "inf"]
                case AtLeast -> request.setMaxPlayers(atLeastMaxPlayersSetting.get(), -1);

                // [0, n]
                case AtMost -> request.setMaxPlayers(0, atMostMaxPlayersSetting.get());

                // [min, max]
                case Between -> request.setMaxPlayers(atLeastMaxPlayersSetting.get(), atMostMaxPlayersSetting.get());

                // [n, n]
                case Equals -> request.setMaxPlayers(equalsMaxPlayersSetting.get());
            }


            switch (geoSearchTypeSetting.get()) {
                case ASN -> request.setAsn(asnNumberSetting.get());
                case Country -> {
                    if (countrySetting.get().name.equalsIgnoreCase("any")) break;
                    request.setCountryCode(countrySetting.get().code);
                }
            }

            request.setCracked(crackedSetting.get().toBoolOrNull());
            request.setDescription(descriptionSetting.get());
            request.setSoftware(softwareSetting.get());

            switch (versionSetting.get()) {
                case Protocol -> request.setProtocolVersion(protocolVersionSetting.get());
                case VersionString -> {
                   Integer protocol = MCVersionUtil.versionToProtocol(versionStringSetting.get());
                   if (protocol == null) {
                       clear();
                       add(theme.label("Unknown version string"));
                       return;
                   }
                   request.setProtocolVersion(protocol);
                }
                case Current -> request.setProtocolVersion(SharedConstants.getProtocolVersion());
            }

            if (!onlineOnlySetting.get()) request.setOnlineAfter(0);
            if (sendsPlayerlist.get()) request.setSendsPlayerlist(true);
            if (onlyBungeeSpoofable.get()) request.setOnlyBungeeSpoofable(true);


            this.locked = true;

            this.threadHasFinished = false;
            this.threadError = null;
            this.threadServers = null;


            MeteorExecutor.execute(() -> {
                String jsonResp = SmallHttp.get("https://api.cornbread2100.com/servers" + request.query());

                var raw = gson.fromJson(jsonResp, ArrayList.class);
//                System.out.println(jsonResp);
//                System.out.println(gson.toJson(servers));
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
//                    servers.add(gson.fromJson(((LinkedTreeMap) el).toString(), Server.class));
                }
                this.threadServers = servers;
                this.threadHasFinished = true;
            });
        };
    }

    @Override
    public void tick() {
        super.tick();
        settings.tick(settingsContainer, theme);

        if (threadHasFinished) handleThreadFinish();

        if (locked) {
            if (timer > 2) {
                findButton.set(getNext(findButton));
                timer = 0;
            }
            else {
                timer++;
            }
        }

        else if (!findButton.getText().equals("Find")) {
            findButton.set("Find");
        }
    }

    @Override
    protected void onClosed() {
        ServerSeeker.COUNTRY_MAP.values().forEach(Country::dispose);
    }

    private String getNext(WButton add) {
        return switch (add.getText()) {
            case "Find", "oo0" -> "ooo";
            case "ooo" -> "0oo";
            case "0oo" -> "o0o";
            case "o0o" -> "oo0";
            default -> "Find";
        };
    }

    private void handleThreadFinish() {
        this.threadHasFinished = false;
        this.locked = false;
        if (this.threadError != null) {
            clear();
            add(theme.label(this.threadError)).expandX();
            WButton backButton = add(theme.button("Back")).expandX().widget();
            backButton.action = this::reload;
            this.locked = false;
            return;
        }
        clear();
        List<Server> servers = this.threadServers;

        if (servers.isEmpty()) {
            add(theme.label("No servers found")).expandX();
            WButton backButton = add(theme.button("Back")).expandX().widget();
            backButton.action = this::reload;
            this.locked = false;
            return;
        }
        add(theme.label("Found " + servers.size() + " servers")).expandX();
        WButton addAllButton = add(theme.button("Add all")).expandX().widget();
        addAllButton.action = () -> {
            for (Server server : servers) {
                String ip = server.ip;

                // Add server to list
                MultiplayerScreenUtil.addNameIpToServerList(multiplayerScreen, "CornSeeker " + ip, ip, false);
            }
            MultiplayerScreenUtil.saveList(multiplayerScreen);

            // Reload widget
            MultiplayerScreenUtil.reloadServerList(multiplayerScreen);

            // Close screen
            if (this.client == null) return;
            client.setScreen(this.multiplayerScreen);
        };

        WTable table = add(theme.table()).widget();

        table.add(theme.label("Server IP"));
        table.add(theme.label("Version"));


        table.row();

        table.add(theme.horizontalSeparator()).expandX();
        table.row();


        for (Server server : servers) {
            final String serverIP = server.ip;
            String serverVersion = server.version.name();

            table.add(theme.label(serverIP));
            table.add(theme.label(serverVersion));

            WButton addServerButton = theme.button("Add Server");
            addServerButton.action = () -> {
                System.out.println(multiplayerScreen.getServerList() == null);
                ServerInfo info = new ServerInfo("CornSeeker " + serverIP, serverIP, ServerInfo.ServerType.OTHER);
                MultiplayerScreenUtil.addInfoToServerList(multiplayerScreen, info);
                addServerButton.visible = false;
            };

            WButton joinServerButton = theme.button("Join Server");
            HostAndPort hap = HostAndPort.fromString(serverIP);

            joinServerButton.action = ()
                -> ConnectScreen.connect(new TitleScreen(), MinecraftClient.getInstance(), new ServerAddress(hap.getHost(), hap.getPort()), new ServerInfo("a", hap.toString(), ServerInfo.ServerType.OTHER), false, null);

            WButton serverInfoButton = theme.button("Server Info");
            serverInfoButton.action = () -> this.client.setScreen(new ServerInfoScreen(serverIP));

            table.add(addServerButton);
            table.add(joinServerButton);
            table.add(serverInfoButton);

            table.row();
        }

        this.locked = false;
    }

    public void saveSettings() {
        savedSettings = sg.toTag();
    }

    public void loadSettings() {
        if (savedSettings == null) return;
        sg.fromTag(savedSettings);
    }

    public void resetSettings() {
        for (Setting<?> setting : sg) {
            setting.reset();
        }
        saveSettings();
        reload();
    }
}
