package de.damcraft.serverseeker.gui;

import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.utils.MultiplayerScreenUtil;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

public class ServerSeekerScreen extends WindowScreen {
    private final MultiplayerScreen multiplayerScreen;

    public ServerSeekerScreen(MultiplayerScreen multiplayerScreen) {
        super(GuiThemes.get(), "CornSeeker");
        this.multiplayerScreen = multiplayerScreen;
    }

    @Override
    public void initWidgets() {
        WHorizontalList accountList = add(theme.horizontalList()).expandX().widget();
        accountList.add(theme.label("Service provided by Cornbread 2100 x OlafcioooX")).expandX();
        WTable userInfoList = add(theme.table()).widget();

        userInfoList.add(theme.label("ur sexiness: "));
        userInfoList.add(theme.label(100 / ServerSeeker.mc.getCurrentFps() + "%"));
        userInfoList.row();

        userInfoList.add(theme.label("ur nick: "));
        userInfoList.add(theme.label(ServerSeeker.mc.getName())).widget().color(Color.WHITE);
        userInfoList.row();

        userInfoList.add(theme.label("ur rizz: "));
        userInfoList.add(theme.label(String.valueOf(multiplayerScreen.getServerList().size()))).widget().color(Color.WHITE);
        userInfoList.row();

        WHorizontalList widgetList = add(theme.horizontalList()).expandX().widget();
        WButton newServersButton = widgetList.add(this.theme.button("Find new servers")).expandX().widget();
        WButton findPlayersButton = widgetList.add(this.theme.button("Search players")).expandX().widget();
        WButton cleanUpServersButton = widgetList.add(this.theme.button("Clean up")).expandX().widget();
        newServersButton.action = () -> {
            if (this.client == null) return;
            this.client.setScreen(new FindNewServersScreen(this.multiplayerScreen));
        };
        findPlayersButton.action = () -> {
            if (this.client == null) return;
            this.client.setScreen(new FindPlayerScreen(this.multiplayerScreen));
        };
        cleanUpServersButton.action = () -> {
            if (this.client == null) return;
            clear();
            add(theme.label("Are you sure you want to clean up your server list?"));
            add(theme.label("This will remove all servers that start with \"CornSeeker\""));
            WHorizontalList buttonList = add(theme.horizontalList()).expandX().widget();
            WButton backButton = buttonList.add(theme.button("Back")).expandX().widget();
            backButton.action = this::reload;
            WButton confirmButton = buttonList.add(theme.button("Confirm")).expandX().widget();
            confirmButton.action = this::cleanUpServers;
        };
    }

    @Override
    public void tick() {}

    public void cleanUpServers() {
        if (this.client == null) return;

        for (int i = 0; i < this.multiplayerScreen.getServerList().size(); i++) {
            if (this.multiplayerScreen.getServerList().get(i).name.startsWith("CornSeeker")) {
                this.multiplayerScreen.getServerList().remove(this.multiplayerScreen.getServerList().get(i));
                i--;
            }
        }

        MultiplayerScreenUtil.saveList(multiplayerScreen);
        MultiplayerScreenUtil.reloadServerList(multiplayerScreen);

        client.setScreen(this.multiplayerScreen);
    }
}
