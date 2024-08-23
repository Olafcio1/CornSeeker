package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ScoreboardPlusPlus extends Module {
    SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<String> title = sgGeneral.add(new StringSetting.Builder()
        .name("title")
        .description("The scoreboard title.")
        .defaultValue("Mountains Of Lava Inc.")
        .build()
    );

    public ScoreboardPlusPlus() {
        super(ServerSeeker.CATEGORY, "Scoreboard++", "Automatically creates a huge scoreboard with specified data.");
    }
}
