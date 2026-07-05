package de.damcraft.serverseeker.modules.crash;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.utils.NbtUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class SpigotSkeletonCrash extends Module {
    public SpigotSkeletonCrash() {
        super(ServerSeeker.C.CRASH, "SpigotSkeletonCrash", "Crashes the server using a skeleton with a flame bow (requires creative/op) (<SPIGOT-7744).");
    }

    @EventHandler
    public void onTick(TickEvent.Post e) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.toggle();

        error("SpigotSkeletonCrash is not done yet because fuck no entitydata im not doing that");
        error("In all seriousness though i have no clue how to do this. in play client a similar module, airstrike, took me a lot of time - because i used that fucking entitydata in it");
        error("So im sorry but this module did not even fucking work anywhere so i do not wanna implement it (for now)");

//        var data = getEntityData();
//
//        if (ServerSeeker.mc.player.isCreative() || ServerSeeker.mc.player.hasInfiniteMaterials()) {
//            var stack = Items.SKELETON_SPAWN_EGG.getDefaultInstance();
//            var compound = NbtUtils.jsonToCompound(data);
//            stack.set(DataComponents.ENTITY_DATA, compound);
//            ServerSeeker.mc.player.addItem(stack);
//            var pos = ServerSeeker.mc.player.blockPosition().offset(0, -1, 0);
//            ServerSeeker.mc.level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
//            stack.useOn(new UseOnContext(
//                ServerSeeker.mc.player,
//                InteractionHand.MAIN_HAND,
//                new BlockHitResult(pos.getCenter(), Direction.DOWN, pos, false)
//            ));
//        } else if (ServerSeeker.mc.player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS))) {
//            ServerSeeker.mc.player.connection.sendCommand("minecraft:summon skeleton ~ ~ ~ " + gson.toJson(data));
//        }
    }

    private static @NotNull JsonObject getEntityData() {
        var data = new JsonObject();
        var hands = new JsonArray();
        var main = new JsonObject();
        main.addProperty("Count", 1);
        main.addProperty("id", "bow");
        var tag = new JsonObject();
        var enchs = new JsonArray();
        var flame = new JsonObject();
        flame.addProperty("id", "flame");
        flame.addProperty("lvl", (short) 1);
        enchs.add(flame);
        tag.add("Enchantments", enchs);
        main.add("tag", tag);
        hands.add(main);
        data.add("HandItems", hands);
        return data;
    }
}
