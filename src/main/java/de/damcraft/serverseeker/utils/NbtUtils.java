package de.damcraft.serverseeker.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.*;

public class NbtUtils {
    public static CompoundTag jsonToCompound(JsonObject json) {
        var compound = new CompoundTag();
        for (var entry : json.entrySet()) {
            var v = entry.getValue();
            var v2 = switch (v) {
                case JsonObject c -> jsonToCompound(c);
                case JsonArray c -> jsonToList(c);
                case JsonPrimitive c -> primitiveToObject(c);
                case null, default -> v;
            };
            compound.put(entry.getKey(), objectToElement(v2));
        }
        return compound;
    }

    public static ListTag jsonToList(JsonArray json) {
        var list = new ListTag();
        for (var e : json)
            if (e instanceof JsonArray c)
                list.add(jsonToList(c));
            else if (e instanceof JsonObject c)
                list.add(jsonToCompound(c));
            else list.add(
                e instanceof JsonPrimitive p ? objectToElement(primitiveToObject(p)) :
                e instanceof JsonArray a     ? jsonToList(a) :
                e instanceof JsonObject o    ? jsonToCompound(o) :
                                               null
            );
        return list;
    }

    public static Object primitiveToObject(JsonPrimitive prim) {
        if (prim.isString())
            return prim.getAsString();
        else if (prim.isBoolean())
            return prim.getAsBoolean();
        else if (prim.isNumber())
            return prim.getAsNumber();
        else if (prim.isJsonObject())
            return prim.getAsJsonObject();
        else if (prim.isJsonArray())
            return prim.getAsJsonArray();
        else if (prim.isJsonPrimitive())
            return prim.getAsJsonPrimitive();
        else// if (prim.isJsonNull())
            return null;
    }

    public static Tag objectToElement(Object obj) {
        return switch (obj) {
            case String c -> StringTag.valueOf(c);
            case Boolean c -> ByteTag.valueOf(c);
            case Number c -> DoubleTag.valueOf(c.doubleValue());
            case JsonObject c -> jsonToCompound(c);
            case JsonArray c -> objectToElement(c.getAsJsonArray());
            case JsonPrimitive c -> objectToElement(primitiveToObject(c.getAsJsonPrimitive()));
            case null, default -> null;
        };
    }
}
