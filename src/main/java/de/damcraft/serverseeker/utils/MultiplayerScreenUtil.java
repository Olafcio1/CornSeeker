package de.damcraft.serverseeker.utils;

import de.damcraft.serverseeker.mixin.MultiplayerScreenAccessor;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;

public class MultiplayerScreenUtil {

    public static void addInfoToServerList(JoinMultiplayerScreen mps, ServerData info) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        mps.getServers().add(info, false);
        mps.getServers().save();
        mpsAccessor.getServerListWidget().updateOnlineServers(mps.getServers());
    }
    public static void addInfoToServerList(JoinMultiplayerScreen mps, ServerData info, boolean reload) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        mps.getServers().add(info, false);
        if (reload) mpsAccessor.getServerListWidget().updateOnlineServers(mps.getServers());
    }

    public static void addNameIpToServerList(JoinMultiplayerScreen mps, String name, String ip) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        ServerData info = new ServerData(name, ip, ServerData.Type.OTHER);
        mps.getServers().add(info, false);
        mpsAccessor.getServerListWidget().updateOnlineServers(mps.getServers());
        mps.getServers().save();
    }
    public static void addNameIpToServerList(JoinMultiplayerScreen mps, String name, String ip, boolean reload) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        ServerData info = new ServerData(name, ip, ServerData.Type.OTHER);
        mps.getServers().add(info, false);
        if (reload) mpsAccessor.getServerListWidget().updateOnlineServers(mps.getServers());
    }

    public static void reloadServerList(JoinMultiplayerScreen mps) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        mpsAccessor.getServerListWidget().updateOnlineServers(mps.getServers());
    }

    public static void saveList(JoinMultiplayerScreen mps) {
        mps.getServers().save();
    }
}
