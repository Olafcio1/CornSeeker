package de.damcraft.serverseeker.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.*;

public class NbtUtils {
    public static NbtCompound jsonToCompound(JsonObject json) {
        var compound = new NbtCompound();
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

    public static NbtList jsonToList(JsonArray json) {
        var list = new NbtList();
        for (var e : json)
            if (e instanceof JsonArray c)
                list.add(jsonToList(c));
            else if (e instanceof JsonObject c)
                list.add(jsonToCompound(c));
            else list.add((NbtElement) e);
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

    public static NbtElement objectToElement(Object obj) {
        return switch (obj) {
            case String c -> NbtString.of(c);
            case Boolean c -> NbtByte.of(c);
            case Number c -> NbtDouble.of(c.doubleValue());
            case JsonObject c -> jsonToCompound(c);
            case JsonArray c -> objectToElement(c.getAsJsonArray());
            case JsonPrimitive c -> objectToElement(primitiveToObject(c.getAsJsonPrimitive()));
            case null, default -> null;
        };
    }
}
