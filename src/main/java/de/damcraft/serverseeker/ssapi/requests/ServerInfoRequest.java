package de.damcraft.serverseeker.ssapi.requests;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class ServerInfoRequest {
    private String ip;
    private Integer port;

    public void setIpPort(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String json() {
        return gson.toJson(this);
    }
}
