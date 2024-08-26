package de.damcraft.serverseeker.ssapi.requests;

import com.google.gson.JsonObject;
import de.damcraft.serverseeker.utils.EncodingUtil;

public class WhereisRequest {
    private enum PlayerSearchType {
        Name,
        Uuid
    }
    private PlayerSearchType playerSearchType;
    public String playerSearchValue;
    public WhereisRequest() {

    }
    public void setName(String name) {
        playerSearchType = PlayerSearchType.Name;
        playerSearchValue = name;
    }

    public void setUuid(String uuid) {
        playerSearchType = PlayerSearchType.Uuid;
        playerSearchValue = uuid;
    }

    public String query() {
        JsonObject jo = new JsonObject();
        jo.addProperty("players.sample.name", playerSearchValue);
        return EncodingUtil.encodeURIComponent(jo.toString());
    }
}
