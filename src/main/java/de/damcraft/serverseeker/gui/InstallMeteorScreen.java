package de.damcraft.serverseeker.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.SmallHttp;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class InstallMeteorScreen extends Screen {
    public InstallMeteorScreen() {
        super(Component.literal("Meteor Client is not installed!"));
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, this.height / 4 - 60 + 20, -1);
    }

    protected void init() {
        super.init();
        Button quitButton = this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), (button) -> {
            this.minecraft.stop();
        }).bounds(this.width / 2 + 2, this.height / 4 + 100 + 25, 148, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Automatically install Meteor (§arecommended§r)"), (button) -> {
            quitButton.active = false;
            CompletableFuture.runAsync(() -> {
                install();
                quitButton.active = true;
            });
        }).bounds(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Manual installation"), (button) -> {
            Util.getPlatform().openUri("https://meteorclient.com/faq/installation");
        }).bounds(this.width / 2 - 150, this.height / 4 + 100 + 25, 148, 20).build());
    }

    private void install() {
        String result = SmallHttp.get("https://meteorclient.com/api/stats");
        if (result == null) {
            this.displayError("Failed to get install meteor automatically! Please install it manually.");
            return;
        }

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(result, JsonObject.class);
        String currentVersion = SharedConstants.getCurrentVersion().name();
        String stableVersion = json.get("mc_version").getAsString();
        String devBuildVersion = json.get("dev_build_mc_version").getAsString();
        String url;
        if (currentVersion.equals(stableVersion)) {
            url = "https://meteorclient.com/api/download";
        } else if (currentVersion.equals(devBuildVersion)) {
            url = "https://meteorclient.com/api/download?devBuild=latest";
        } else {
            this.displayError("Failed to find Meteor for your current version.");
            return;
        }
        HttpResponse<InputStream> file = SmallHttp.download(url);
        if (file == null) {
            this.displayError("Failed to download Meteor! Please install it manually.");
            return;
        }
        Optional<String> filenameT = file.headers().firstValue("Content-Disposition");
        String filename = "meteor-client.jar";
        if (filenameT.isPresent()) {
            String[] parts = filenameT.get().split("; ");
            for (String part : parts) {
                if (part.startsWith("filename=")) {
                    filename = part.substring(9);
                    break;
                }
            }
            if (filename.startsWith("\"") && filename.endsWith("\"")) {
                filename = filename.substring(1, filename.length() - 1);
            }
        }

        // Get the mods folder
        Path modsFolder = FabricLoader.getInstance().getGameDir().resolve("mods");
        if (!Files.exists(modsFolder)) {
            this.displayError("Failed to find mods folder! Please install Meteor manually.");
            return;
        }

        // Save the file
        try {
            Files.copy(file.body(), modsFolder.resolve(filename));
        } catch (Exception e) {
            this.displayError("Failed to save Meteor! Please install it manually.");
            e.printStackTrace();
            return;
        }

        // Success message
        this.displayNotice("Successfully installed Meteor! Please restart your game.");
    }

    private void displayError(String errorMessage) {
        this.displayNotice(errorMessage);

        this.addRenderableWidget(Button.builder(Component.literal("Open install FAQ"), (button2) -> {
            Util.getPlatform().openUri("https://meteorclient.com/faq/installation");
        }).bounds(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
    }

    private void displayNotice(String noticeMessage) {
        this.clearWidgets();
        this.addRenderableWidget(new StringWidget(
            this.width / 2 - 250,
            this.height / 4,
            500,
            20,
            Component.literal(noticeMessage),
            this.font
        ));
    }
}
