package de.damcraft.serverseeker.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class TextUtil {
    public static String smallCaps(String text) {
        return text
            .replace("q", "ᴏ̨")
            .replace("w", "ᴡ")
            .replace("e", "ᴇ")
            .replace("r", "ʀ")
            .replace("t", "ᴛ")
            .replace("y", "ʏ")
            .replace("u", "ᴜ")
            .replace("i", "ɪ")
            .replace("o", "ᴏ")
            .replace("p", "ᴘ")
            .replace("a", "ᴀ")
//            .replace("s", "s")
            .replace("d", "ᴅ")
            .replace("f", "ғ")
            .replace("g", "ɢ")
            .replace("h", "ʜ")
            .replace("j", "ᴊ")
            .replace("k", "ᴋ")
            .replace("l", "ʟ")
            .replace("z", "ᴢ")
//            .replace("x", "x")
            .replace("c", "ᴄ")
            .replace("v", "ᴠ")
            .replace("b", "ʙ")
            .replace("n","ɴ")
            .replace("m", "ᴍ");
    }

    public static class MineText {
        private final JsonArray json;

        MineText(JsonArray json) {
            this.json = json;
        }

        public MineText pass(Function<String, String> func) {
            var b = new JsonArray();
            for (var obj : this.json) {
                var o = obj.deepCopy().getAsJsonObject();
                o.addProperty("text", func.apply(o.get("text").getAsString()));
                b.add(o);
            }
            return new MineText(b);
        }

        public MineText smallcaps() {
            return pass(TextUtil::smallCaps);
        }

        public MineText starscript() {
            return pass(value -> MeteorStarscript.run(MeteorStarscript.compile(value)));
        }

        public String toString() {
            return gson.toJson(this.json);
        }
    }

    public static MineText colorsJson(String text) {
        var data = new JsonArray();
        var current = "";

        var spl = text.split("");
        var inColor = false;
        var styles = new TreeMap<String, String>(){{
            this.put("c", "color:red");
            this.put("4", "color:dark_red");
            this.put("6", "color:orange");
            this.put("e", "color:yellow");
            this.put("a", "color:light_green");
            this.put("2", "color:dark_green");
            this.put("b", "color:??");
            this.put("9", "color:blue");
            this.put("3", "color:???");
            this.put("1", "color:dark_blue");
            this.put("d", "color:???");
            this.put("5", "color:???");
            this.put("7", "color:gray");
            this.put("8", "color:dark_gray");
            this.put("0", "color:black");
            this.put("f", "color:white");
            this.put("l", "bold:true");
            this.put("o", "italic:true");
            this.put("n", "underline:true");
            this.put("m", "strikethrough:true");
            this.put("k", "obfuscated:true");

        }};
//        var styles = new ArrayList<String>(){{
//            "c", // red
//            "4", // darker red
//            "6", // orange
//            "e", // yellow
//            "a", // light green
//            "2", // dark green
//            "b", // light blue
//            "9", // weird blue
//            "3", // tab blue
//            "1", // dark blue
//            // the amount of blue variants lmfao
//            "d", // pink
//            "5", // violet
//            "7", // light gray
//            "8", // dark gray
//            "0", // black
//            "f", // white
//
//            "l", // bold
//            "o", // italic
//            "n", // underline
//            "m", // strikethrough
//            "k" // uhhh some weird shit crap from enchants ig
//        }};
        var applied = new JsonObject();
        for (var ch : spl) {
            if (inColor) {
                if (styles.get(ch) != null) {
                    var s = styles.get(ch).split(":");
                    if (s[0] == "color") {
                        if (current != "") {
                            var obj = new JsonObject();
                            obj.addProperty("text", current);
                            for (var e : applied.entrySet())
                                obj.add(e.getKey(), e.getValue());
                            data.add(current);
                            current = "";
                        }
                    } else if (s[1] == "true")
                        applied.addProperty(s[0], true);
                    else if (s[1] == "false")
                        applied.addProperty(s[0], false);
                    else applied.addProperty(s[0], s[1]);
                } else current += "&" + ch;
            } else if (ch == "&") {
                inColor = true;
            } else {
                current += ch;
            }
        }
        var obj = new JsonObject();
        obj.addProperty("text", current);
        for (var e : applied.entrySet())
            obj.add(e.getKey(), e.getValue());
        data.add(current);

        return new MineText(data);
    }
}
