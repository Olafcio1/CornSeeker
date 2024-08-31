package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class PaperBookDupe extends Module {
    public PaperBookDupe() {
        super(ServerSeeker.C.MAIN, "PaperBookDupe", "Dupes your full inventory by using a book and quill. Before doing this, you must reconnect.");
    }

    @EventHandler
    public void onTick(TickEvent.Post e) throws NoSuchFieldException, IllegalAccessException {
        this.toggle();
        var hand = ServerSeeker.mc.player.getMainHandStack();
        if (hand.getItem() != Items.WRITABLE_BOOK) {
            ServerSeeker.mc.player.sendMessage(Text.of("[CornSeeker] To do PaperBookDupe, you must hold a book and quil in your main hand!"));
            return;
        }
//        hand.use(ServerSeeker.mc.world, ServerSeeker.mc.player, Hand.MAIN_HAND);
        ServerSeeker.mc.player.useBook(hand, Hand.MAIN_HAND);
        var screen = ServerSeeker.mc.currentScreen;
        if (screen instanceof BookScreen book) {
            var ch = book.getClass().getDeclaredField("contents");
            var contents = (BookScreen.Contents) ch.get(BookScreen.Contents.class);
            contents.pages().add(Text.of(".gg/4dmqcVfQjA OLAFCIO.PL ON FUCKING TOP YA"));
            ServerSeeker.mc.player.getInventory().dropAll();
        } else {
            ServerSeeker.mc.player.sendMessage(Text.of("[CornSeeker] Something fucked up in PaperBookDupe!"));
        }
    }
}
