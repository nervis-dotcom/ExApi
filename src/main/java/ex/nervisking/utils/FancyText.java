package ex.nervisking.utils;

import ex.nervisking.ModelManager.TextStyle;

import java.util.HashMap;
import java.util.Map;

public class FancyText {

    private static final String from = "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ";

    public static String convertText(String text, TextStyle style) {
        if (style == null || style == TextStyle.NONE) return text;

        Map<Character, Character> map = getMapForStyle(style);
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            Character convertedChar = map.getOrDefault(c, c);
            result.append(convertedChar);
        }
        return result.toString();
    }

    private static Map<Character, Character> getMapForStyle(TextStyle style) {
        return switch (style) {
            case SMALLCAPS -> createSmallCapsMap();
            case ACCENT -> createAccentMap();
            case BIG -> createBigMap();
            case BUBBLE -> createBubbleMap();
            case CURRENCY -> createCurrencyMap();
            case NONE -> new HashMap<>();
        };
    }

    private static Map<Character, Character> createSmallCapsMap() {
        Map<Character, Character> map = new HashMap<>();
        String to = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴñᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴñᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";
        for (int i = 0; i < from.length(); i++) {
            map.put(from.charAt(i), to.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createAccentMap() {
        Map<Character, Character> map = new HashMap<>();
        String to = "ābčďéfǥĥɨĵķłmņŇǒpqřşŧùvŵxŷžĀBÇÐÊFǴĦÎĴĶĿMŇήÖPQŘŞŢŬVŴXŸƵ";
        for (int i = 0; i < from.length(); i++) {
            map.put(from.charAt(i), to.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createBigMap() {
        Map<Character, Character> map = new HashMap<>();
        String to = "ᗩᗷᑕᗪEᖴGᕼIᒍKᒪᗰᑎñOᑭᑫᖇᔕTᑌᐯᗯ᙭YᘔᗩᗷᑕᗪEᖴGᕼIᒍKᒪᗰᑎÑOᑭᑫᖇᔕTᑌᐯᗯ᙭Yᘔ";
        for (int i = 0; i < from.length(); i++) {
            map.put(from.charAt(i), to.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createBubbleMap() {
        Map<Character, Character> map = new HashMap<>();
        String to = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ";
        for (int i = 0; i < from.length(); i++) {
            map.put(from.charAt(i), to.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createCurrencyMap() {
        Map<Character, Character> map = new HashMap<>();
        String to = "₳฿₵ĐɆ₣₲ⱧłJ₭Ⱡ₥₦ñØ₱QⱤ₴₮ɄV₩ӾɎⱫ₳฿₵ĐɆ₣₲ⱧłJ₭Ⱡ₥₦ÑØ₱QⱤ₴₮ɄV₩ӾɎⱫ";
        for (int i = 0; i < from.length(); i++) {
            map.put(from.charAt(i), to.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }
}
