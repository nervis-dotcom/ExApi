package ex.nervisking.utils;

import ex.nervisking.ModelManager.TextStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FancyText {

    private static final String from = "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789";

//    public static String convertText(String text, TextStyle style) {
//        if (style == null || style == TextStyle.NONE) return text;
//
//        Map<Integer, String> map = getMapForStyle(style);
//        StringBuilder result = new StringBuilder();
//        text.codePoints().forEach(cp -> {
//            System.out.printf("U+%04X -> ", cp);
//            String converted = map.getOrDefault(cp, new String(Character.toChars(cp)));
//            System.out.printf("%s (U+%s)%n", converted,
//                    converted.codePoints()
//                            .mapToObj(cp2 -> String.format("%04X", cp2))
//                            .collect(Collectors.joining(",")));
//            result.append(converted);
//        });
//
//        return result.toString();
//    }

    public static String convertText(String text, TextStyle style) {
        if (style == null || style == TextStyle.NONE) return text;

        Map<Integer, String> map = getMapForStyle(style);
        StringBuilder result = new StringBuilder();

        text.codePoints().forEach(cp -> {
            if (map.containsKey(cp)) {
                result.append(map.get(cp));
            } else {
                result.append(new String(Character.toChars(cp))); // deja emojis y símbolos intactos
            }
        });

        return result.toString();
    }


    private static Map<Integer, String> getMapForStyle(TextStyle style) {
        return switch (style) {
            case SMALLCAPS -> createSmallCapsMap();
            case ACCENT -> createAccentMap();
            case BIG -> createBigMap();
            case BUBBLE -> createBubbleMap();
            case CURRENCY -> createCurrencyMap();
            case NONE -> new HashMap<>();
        };
    }

    private static Map<Integer, String> createSmallCapsMap() {
        Map<Integer, String> map = new HashMap<>();
        String to = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴñᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴñᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ⁰¹²³⁴⁵⁶⁷⁸⁹";
        for (int i = 0; i < from.length(); i++) {
            map.put((int) from.charAt(i), String.valueOf(to.charAt(i)));
        }
        map.put((int) ' ', " ");
        return map;
    }

    private static Map<Integer, String> createAccentMap() {
        Map<Integer, String> map = new HashMap<>();
        String to = "ābčďéfǥĥɨĵķłmņŇǒpqřşŧùvŵxŷžĀBÇÐÊFǴĦÎĴĶĿMŇήÖPQŘŞŢŬVŴXŸƵ₀₁₂₃₄₅₆₇₈₉";
        for (int i = 0; i < from.length(); i++) {
            map.put((int) from.charAt(i), String.valueOf(to.charAt(i)));
        }
        map.put((int) ' ', " ");
        return map;
    }

    private static Map<Integer, String> createBigMap() {
        Map<Integer, String> map = new HashMap<>();
        String to = "ᗩᗷᑕᗪEᖴGᕼIᒍKᒪᗰᑎñOᑭᑫᖇᔕTᑌᐯᗯ᙭YᘔᗩᗷᑕᗪEᖴGᕼIᒍKᒪᗰᑎÑOᑭᑫᖇᔕTᑌᐯᗯ᙭Yᘔ0123456789";
        for (int i = 0; i < from.length(); i++) {
            map.put((int) from.charAt(i), String.valueOf(to.charAt(i)));
        }
        map.put((int) ' ', " ");
        return map;
    }

    private static Map<Integer, String> createBubbleMap() {
        Map<Integer, String> map = new HashMap<>();
        String to = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ⓪①②③④⑤⑥⑦⑧⑨";
        for (int i = 0; i < from.length(); i++) {
            map.put((int) from.charAt(i), String.valueOf(to.charAt(i)));
        }
        map.put((int) ' ', " ");
        return map;
    }

    private static Map<Integer, String> createCurrencyMap() {
        Map<Integer, String> map = new HashMap<>();
        String to = "₳฿₵ĐɆ₣₲ⱧłJ₭Ⱡ₥₦ñØ₱QⱤ₴₮ɄV₩ӾɎⱫ₳฿₵ĐɆ₣₲ⱧłJ₭Ⱡ₥₦ÑØ₱QⱤ₴₮ɄV₩ӾɎⱫ⓿❶❷❸❹❺❻❼❽❾";
        for (int i = 0; i < from.length(); i++) {
            map.put((int) from.charAt(i), String.valueOf(to.charAt(i)));
        }
        map.put((int) ' ', " ");
        return map;
    }
}
