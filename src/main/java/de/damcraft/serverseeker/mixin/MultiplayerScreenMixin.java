package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.gui.GetInfoScreen;
import de.damcraft.serverseeker.gui.ServerSeekerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
    @Shadow
    protected ServerSelectionList serverSelectionList;
    private Button getInfoButton;

    protected MultiplayerScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/multiplayer/JoinMultiplayerScreen;onSelectedChange()V"))
    private void onInit(CallbackInfo info) {
        // Add a button which sets the current screen to the ServerSeekerScreen
        this.addRenderableWidget(
            new Button.Builder(
                Component.literal("ServerSeeker"),
                onPress -> {
                    if (this.minecraft == null) return;
                    this.minecraft.setScreen(new ServerSeekerScreen((JoinMultiplayerScreen) (Object) this));
                }
            )
                .pos(150, 3)
                .width(80)
                .build()
        );

        // Add a button to get the info of the selected server
        this.getInfoButton = this.addRenderableWidget(
            new Button.Builder(
                Component.literal("Get players"),
                onPress -> {
                    if (this.minecraft == null) return;
                    ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
                    if (entry != null) {
                        if (this.minecraft == null) return;
                        this.minecraft.setScreen(new GetInfoScreen((JoinMultiplayerScreen) (Object) this, entry));
                    }
                }
            )
                .pos(150 + 80 + 5, 3)
                .width(80)
                .build()
        );
    }

    @Inject(method = "onSelectedChange", at = @At("TAIL"))
    private void onUpdateButtonActivationStates(CallbackInfo info) {
        // Enable the button if a server is selected
        ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
        this.getInfoButton.active = entry != null && !(entry instanceof ServerSelectionList.LANHeader);
    }
}
