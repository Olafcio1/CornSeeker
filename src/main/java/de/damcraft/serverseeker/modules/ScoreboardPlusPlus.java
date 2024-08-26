package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.utils.TextUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Scoreboard++
 * Yes I know I've skidded this code from trouserstreak.
 * BUT I improved it: I added full coloring & auto full width.
 */
public class ScoreboardPlusPlus extends Module {
    private final SettingGroup sgTitle = settings.createGroup("Title Options");
    private final SettingGroup sgContent = settings.createGroup("Content Options");
    private final SettingGroup sgDisplay = settings.createGroup("Display Options");

    private final Setting<String> title = sgTitle.add(new StringSetting.Builder()
        .name("title")
        .description("Title of the scoreboard to create. Supports Starscript.")
        .defaultValue("&c&l=== Mountains Of Lava Inc. ===")
        .wide()
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<List<String>> content = sgContent.add(new StringListSetting.Builder()
        .name("content")
        .description("Content of the scoreboard. Supports Starscript.")
        .defaultValue(Arrays.asList(
            "",
            "&6Group YT:  &6Griefing content!",
            "&c&nyoutube.com/@mountainsoflavainc.6913",
            "",
            "&6Mine YT:  &6Various things.",
            "&c&nyoutube.com/@olafcio",
            "",
            "&9Destroyed by &lOlafcio  &6lmfao",
            "&7{date}"
        ))
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<Boolean> fullWidth = sgDisplay.add(new BoolSetting.Builder()
        .name("fullwidth")
        .description("If true, makes the scoreboard fill out the whole screen (use 2 spaces).")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> prettyFont = sgDisplay.add(new BoolSetting.Builder()
        .name("prettyfont")
        .description("If true, makes the scoreboard use a pretty font.")
        .defaultValue(true)
        .build()
    );

    public ScoreboardPlusPlus() {
        super(ServerSeeker.CATEGORY, "scoreboard++", "Automatically creates a huge scoreboard with specified data. Credits to TrouserStreak.");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        var scoreboardName = RandomStringUtils.randomAlphabetic(10).toLowerCase();

        var title = this.title.get();
        var data = TextUtil.colorsJson(title);
//        title = MeteorStarscript.run(MeteorStarscript.compile(title));
        data = data.starscript();
        if (prettyFont.get())
            data = data.smallcaps();
        title = data.toString();

        var cmd = "/scoreboard objectives add " + scoreboardName + " dummy " + title;
        if (cmd.length() <= 256) {
            ChatUtils.sendPlayerMsg(cmd);
        } else {
            var removeChars = cmd.length()-256;
            error("Title is too long. Shorten it by "+removeChars+" characters.");
            toggle();
            return;
        }

        ChatUtils.sendPlayerMsg("/scoreboard objectives setdisplay sidebar " + scoreboardName);
        ChatUtils.sendPlayerMsg("/scoreboard objectives modify " + scoreboardName + " numberformat blank");

        var i = content.get().size();
        for (var string : content.get()) {
            var team = RandomStringUtils.randomAlphabetic(10).toLowerCase();
            ChatUtils.sendPlayerMsg("/team add " + team);

            if (fullWidth.get())
                string = string.replace("  ", " ".repeat(129 - string.length() + 2));

            if (!Objects.equals(string, "")) {
                var data2 = TextUtil.colorsJson(" " + title);
//                string = MeteorStarscript.run(MeteorStarscript.compile(string));
                data2 = data2.starscript();
                if (prettyFont.get())
                    data2 = data2.smallcaps();
                string = data2.toString();
            }

            var cmd2 = "/team modify " + team + " suffix " + string;
            if (cmd2.length() <= 256) {
                ChatUtils.sendPlayerMsg(cmd2);
            } else {
                var removeChars = cmd2.length()-256;
                error("This content line is too long ("+ MeteorStarscript.run(MeteorStarscript.compile(string))+"). Shorten it by "+removeChars+" characters.");
                toggle();
                return;
            }

            ChatUtils.sendPlayerMsg("/team modify " + team + " color black");
            ChatUtils.sendPlayerMsg("/team join " + team + " " + i);
            ChatUtils.sendPlayerMsg("/scoreboard players set " + i + " " + scoreboardName + " " + i);
            i--;
        }
        toggle();
        info("Created scoreboard.");
    }
}
