package de.damcraft.serverseeker;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import de.damcraft.serverseeker.country.Countries;
import de.damcraft.serverseeker.country.Country;
import de.damcraft.serverseeker.country.CountrySetting;
import de.damcraft.serverseeker.hud.HistoricPlayersHud;
import de.damcraft.serverseeker.modules.BungeeSpoofModule;
import de.damcraft.serverseeker.modules.HideFromMCLOutput;
import de.damcraft.serverseeker.modules.OPRegionBypass;
import de.damcraft.serverseeker.modules.ScoreboardPlusPlus;
import de.damcraft.serverseeker.modules.crash.SpigotSkeletonCrash;
import de.damcraft.serverseeker.utils.HistoricPlayersUpdater;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.slf4j.Logger;

import java.util.Map;

public class ServerSeeker extends MeteorAddon {
    /*
    Feature list for anticope.pages.dev:
    (creates features matching the RegEx '(?:add\(new )([^(]+)(?:\([^)]*)\)\)', as anticope checks for that.
    add(new Find servers with many parameters, for example: Cracked, Description, Player count, much more...())
    add(new Server database with around 1.000.000 servers!())
    add(new Over 80.000.000 players tracked!())
    add(new Search for ANY server you want!())
    add(new Join misconfigured BungeeCord backends with any name you want!())
     */
    public static final Logger LOG = LogUtils.getLogger();

    public enum C {
        ;
        public static final Category MAIN = new Category("CornSeeker", Items.SPYGLASS.getDefaultStack());
        public static final Category CRASH = new Category("Crash", Items.BARRIER.getDefaultStack());
    }

    public static final Map<String, Country> COUNTRY_MAP = new Object2ReferenceOpenHashMap<>();

    public static final Gson gson = new Gson();

    public static MinecraftClient mc;

    @Override
    public void onInitialize() {
        LOG.info("Loaded the CornSeeker addon! Seek my pipi!");

        // Load countries
        Countries.init();

        var mods = Modules.get();
        mods.add(new BungeeSpoofModule());
        mods.add(new ScoreboardPlusPlus());
        mods.add(new OPRegionBypass());
        mods.add(new HideFromMCLOutput());
        mods.add(new SpigotSkeletonCrash());
        Hud.get().register(HistoricPlayersHud.INFO);

        SettingsWidgetFactory.registerCustomFactory(CountrySetting.class, (theme) -> (table, setting) -> {
            CountrySetting.countrySettingW(table, (CountrySetting) setting, theme);
        });

        MeteorClient.EVENT_BUS.subscribe(HistoricPlayersUpdater.class);
    }
    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(C.MAIN);
        Modules.registerCategory(C.CRASH);
    }

    @Override
    public String getPackage() {
        return "de.damcraft.serverseeker";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Olafcio1", "CornSeeker");
    }

    @Override
    public String getWebsite() {
        return "https://olafcio1.github.io/";
    }

    @Override
    public String getCommit() {
        String commit = FabricLoader
            .getInstance()
            .getModContainer("cornseeker")
            .get().getMetadata()
            .getCustomValue("github:sha")
            .getAsString();
        return commit.isEmpty() ? null : commit.trim();
    }
}
