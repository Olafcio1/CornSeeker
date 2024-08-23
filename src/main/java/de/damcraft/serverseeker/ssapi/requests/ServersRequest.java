package de.damcraft.serverseeker.ssapi.requests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.EncodingUtil;

import java.sql.Time;
import java.util.Calendar;
import java.util.Objects;

public class ServersRequest {
    private Integer asn;
    private String country_code;
    private Boolean cracked;
    private String description;
    private JsonArray max_players;
    private Integer online_after;
    private JsonArray online_players;
    private Integer protocol;
    private Boolean sends_playerlist;
    private Boolean only_bungeespoofable;

    private String software;

    public void setAsn(Integer asn) {
        this.asn = asn;
    }

    public void setCountryCode(String cc) {
        this.country_code = cc;
    }

    public void setCracked(Boolean cracked) {
        this.cracked = cracked;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxPlayers(Integer exact) {
        this.max_players = new JsonArray();
        this.max_players.add(exact);
        this.max_players.add(exact);
    }

    public void setMaxPlayers(Integer min, Integer max) {
        this.max_players = new JsonArray();
        this.max_players.add(min);
        if (max == -1) {
            max_players.add("inf");
        }
        else {
            this.max_players.add(max);
        }
    }

    public void setOnlineAfter(Integer unix_timestamp) {
        this.online_after = unix_timestamp;
    }

    public void setOnlinePlayers(Integer exact) {
        this.online_players = new JsonArray();
        this.online_players.add(exact);
        this.online_players.add(exact);
    }

    public void setOnlinePlayers(Integer min, Integer max) {
        this.online_players = new JsonArray();
        this.online_players.add(min);
        if (max == -1) {
            this.online_players.add("inf");
        }
        else {
            this.online_players.add(max);
        }
    }

    public void setProtocolVersion(Integer version) {
        this.protocol = version;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public void setSendsPlayerlist(Boolean sends) {
        this.sends_playerlist = sends;
    }

    public void setOnlyBungeeSpoofable(Boolean only) {
        this.only_bungeespoofable = only;
    }

    public String query() {
        var data = new JsonObject();
        if (online_players != null) {
            var min = online_players.get(0);
            var max = online_players.get(1);

            Integer minInt = null;
            try {
                minInt = min.getAsInt();
            } catch (Throwable ignored) {}
            Integer maxInt = null;
            try {
                maxInt = max.getAsInt();
            } catch (Throwable ignored) {}

            if (min == max) {
                if (minInt != null)
                    data.addProperty("players.online", minInt);
            } else {
                var obj = new JsonObject();
                if (minInt != null)
                    obj.addProperty("$gte", minInt);
                if (maxInt != null)
                    obj.addProperty("$lte", maxInt);
                data.add("players.online", obj);
            }
        }
        if (max_players != null) {
            var min = max_players.get(0);
            var max = max_players.get(1);

            Integer minInt = null;
            try {
                minInt = min.getAsInt();
            } catch (Throwable ignored) {}
            Integer maxInt = null;
            try {
                maxInt = max.getAsInt();
            } catch (Throwable ignored) {}

            if (min == max) {
                if (minInt != null)
                    data.addProperty("players.max", minInt);
            } else {
                var obj = new JsonObject();
                if (minInt != null)
                    obj.addProperty("$gte", minInt);
                if (maxInt != null)
                    obj.addProperty("$lte", maxInt);
                data.add("players.max", obj);
            }
        }
//        if (isFullEnabled) {
//            if (isFull.checked) mongoFilter['$expr'] = { '$eq': ['$players.online', '$players.max'] };
//        else mongoFilter['$expr'] = { '$ne': ['$players.online', '$players.max'] };
//        }
//        if (onlinePlayerEnabled) mongoFilter['players.sample.name'] = onlinePlayer.value;
//        if (pastPlayerEnabled) mongoFilter['players.sample.name'] = pastPlayer.value;
        if (!Objects.equals(software, "")) {
            var obj = new JsonObject();
            obj.addProperty("$regex", software);
            obj.addProperty("$options", "i");
            data.add("version.name", obj);
        }
        if (protocol != null)
            data.addProperty("version.protocol", protocol);
        if (!Objects.equals(description, "")) {
            var obj = new JsonArray();

            var object = new JsonObject(); // {'$regex': description.value, '$options': 'i'} what the actual fuck
            object.addProperty("$regex", description);
            object.addProperty("$options", "i");

            var desc = new JsonObject(); // {'description': {'$regex': description.value, '$options': 'i'}}
            desc.add("description", object);
            obj.add(desc);

            var text = new JsonObject(); // {'description.text': {'$regex': description.value, '$options': 'i'}}
            text.add("description.text", object);
            obj.add(text);

            var extra = new JsonObject(); // {'description.extra.text': {'$regex': description.value, '$options': 'i'}}
            extra.add("description.extra.text", object);
            obj.add(extra);

            data.add("$or", obj);
        }
        if (sends_playerlist != null && sends_playerlist) {
            var sample = new JsonObject();
            sample.addProperty("$exists", true);
            var ns = new JsonObject();
            ns.addProperty("$size", 0);
            sample.add("$not", ns);
            data.add("players.sample", sample);
        }
        var lastseen = new JsonObject();
        lastseen.addProperty("$gte", Calendar.getInstance().getTime().getTime() - Calendar.HOUR);
//        data.add("lastSeen", lastseen);
//        if (ipSubnetEnabled) {
//            const [ip, range] = ipSubnet.value.split('/');
//            const ipCount = 2**(32 - range)
//            const octets = ip.split('.');
//            for (var i = 0; i < octets.length; i++) {
//                if (256**i < ipCount) {
//                    var min = octets[octets.length - i - 1];
//                    var max = 255;
//                    if (256**(i + 1) < ipCount) {
//                        min = 0;
//                    } else {
//                        max = ipCount / 256;
//                    }
//                    octets[octets.length - i - 1] = `(${min}|[1-9]\\d{0,2}|[1-9]\\d{0,1}\\d|${max})`;
//                }
//            }
//
//            mongoFilter['ip'] = { '$regex': `^${octets[0]}\.${octets[1]}\.${octets[2]}\.${octets[3]}\$`, '$options': 'i' }
//        }
//        if (portEnabled) mongoFilter['port'] = port.value;
        if (cracked != null) data.addProperty("cracked", cracked);
        var dataStr = new Gson().toJson(data);
        return "?query=" + EncodingUtil.encodeURIComponent(dataStr);//{%22players.online%22:{%22$gte%22:1,%22$lte%22:11},%22players.max%22:2,%22$expr%22:{%22$ne%22:[%22$players.online%22,%22$players.max%22]},%22players.sample.name%22:%22HackerFC12%22,%22version.name%22:{%22$regex%22:%22^Vanilla%22,%22$options%22:%22i%22},%22version.protocol%22:757,%22$or%22:[{%22description%22:{%22$regex%22:%22testing!%22,%22$options%22:%22i%22}},{%22description.text%22:{%22$regex%22:%22testing!%22,%22$options%22:%22i%22}},{%22description.extra.text%22:{%22$regex%22:%22testing!%22,%22$options%22:%22i%22}}],%22players.sample%22:{%22$exists%22:true,%22$not%22:{%22$size%22:0}},%22lastSeen%22:{%22$gte%22:1724400000},%22ip%22:{%22$regex%22:%22^test.undefined.undefined.undefined$%22,%22$options%22:%22i%22},%22cracked%22:true}";
    }
}
