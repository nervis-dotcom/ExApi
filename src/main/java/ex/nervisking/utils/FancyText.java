package ex.nervisking.utils;

import ex.nervisking.ModelManager.TextStyle;

import java.util.HashMap;
import java.util.Map;

public class FancyText {

    private static final String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Mapas de caracteres estilizados
    private static final Map<Character, Character> gothicMap = createGothicMap();
    private static final Map<Character, Character> scriptMap = createScriptMap();
    private static final Map<Character, Character> doubleStruckMap = createDoubleStruckMap();
    private static final Map<Character, Character> circledMap = createCircledMap();
    private static final Map<Character, Character> squaredMap = createSquaredMap();
    private static final Map<Character, Character> boldMap = createBoldMap();
    private static final Map<Character, Character> italicMap = createItalicMap();
    private static final Map<Character, Character> sansSerifMap = createSansSerifMap();
    private static final Map<Character, Character> runicMap = createRunicMap();
    private static final Map<Character, Character> arabicMap = createArabicMap();

    public static String convertText(String text, TextStyle style) {
        if (style == null) return text;

        Map<Character, Character> map = getMapForStyle(style);
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            Character convertedChar = map.getOrDefault(c, c);
            // Solo aviso para caracteres que no sean espacio
            if (convertedChar == c && c != ' ') {
                System.out.println("No conversion found for: " + c);
            }
            result.append(convertedChar);
        }
        return result.toString();
    }

    private static Map<Character, Character> getMapForStyle(TextStyle style) {
        return switch (style) {
            case GOTHIC -> gothicMap;
            case SCRIPT -> scriptMap;
            case DOUBLE_STRUCK -> doubleStruckMap;
            case CIRCLED -> circledMap;
            case SQUARED -> squaredMap;
            case BOLD -> boldMap;
            case ITALIC -> italicMap;
            case SANS_SERIF -> sansSerifMap;
            case RUNIC -> runicMap;
            case ARABIC -> arabicMap;
            default -> new HashMap<>();
        };
    }

    private static Map<Character, Character> createGothicMap() {
        Map<Character, Character> map = new HashMap<>();
        String gothic = "𝔞𝔟𝔠𝔡𝔢𝔣𝔤𝔥𝔦𝔧𝔨𝔩𝔪𝔫𝔬𝔭𝔮𝔯𝔰𝔱𝔲𝔳𝔴𝔵𝔶𝔷𝔄𝔅ℭ𝔇𝔈𝔉𝔊ℌℑ𝔍𝔎𝔏𝔐𝔑𝔒𝔓𝔔ℜ𝔖𝔗𝔘𝔙𝔚𝔛𝔜𝔝";
        // Ajusta esta cadena para tener 52 caracteres, aquí una versión corregida:
        gothic = "𝔞𝔟𝔠𝔡𝔢𝔣𝔤𝔥𝔦𝔧𝔨𝔩𝔪𝔫𝔬𝔭𝔮𝔯𝔰𝔱𝔲𝔳𝔴𝔵𝔶𝔷" +
                "𝔄𝔅ℭ𝔇𝔈𝔉𝔊ℌℑ𝔍𝔎𝔏𝔐𝔑𝔒𝔓𝔔ℜ𝔖𝔗𝔘𝔙𝔚𝔛𝔜𝔝";

        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), gothic.charAt(i));
        }
        map.put(' ', ' '); // añade espacio para evitar errores
        return map;
    }

    private static Map<Character, Character> createScriptMap() {
        Map<Character, Character> map = new HashMap<>();
        String script = "𝓪𝓫𝓬𝓭𝓮𝓯𝓰𝓱𝓲𝓳𝓴𝓵𝓶𝓷𝓸𝓹𝓺𝓻𝓼𝓽𝓾𝓿𝔀𝔁𝔂𝔃" +
                "𝓐𝓑𝓒𝓓𝓔𝓕𝓖𝓗𝓘𝓙𝓚𝓛𝓜𝓝𝓞𝓟𝓠𝓡𝓢𝓣𝓤𝓥𝓦𝓧𝓨𝓩";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), script.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createDoubleStruckMap() {
        Map<Character, Character> map = new HashMap<>();
        String doubleStruck = "𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫" +
                "𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), doubleStruck.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createCircledMap() {
        Map<Character, Character> map = new HashMap<>();
        String circled = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ" +
                "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), circled.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createSquaredMap() {
        Map<Character, Character> map = new HashMap<>();
        // Aquí te recomiendo usar solo caracteres cuadrados ASCII para evitar problemas,
        // porque esos emojis que usas pueden no mapear bien.
        // Ejemplo básico:
        String squared = "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉" +
                "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), squared.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createBoldMap() {
        Map<Character, Character> map = new HashMap<>();
        String bold = "𝗮𝗯𝗰𝗱𝗲𝗳𝗴𝗵𝗶𝗷𝗸𝗹𝗺𝗻𝗼𝗽𝗾𝗿𝘀𝘵𝘂𝘃𝘄𝘅𝘆𝘇" +
                "𝗔𝗕𝗖𝗗𝗘𝗙𝗚𝗛𝗜𝗝𝗞𝗟𝗠𝗡𝗢𝗣𝗤𝗥𝗦𝗧𝗨𝗩𝗪𝗫𝗬𝗭";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), bold.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createItalicMap() {
        Map<Character, Character> map = new HashMap<>();
        String italic = "𝘢𝘣𝘤𝘥𝘦𝘧𝘨𝘩𝘪𝘫𝘬𝘭𝘮𝘯𝘰𝘱𝘲𝘳𝘴𝘵𝘶𝘷𝘸𝘹𝘺𝘻" +
                "𝘈𝘉𝘊𝘋𝘌𝘍𝘎𝘏𝘐𝘑𝘒𝘓𝘔𝘕𝘖𝘗𝘘𝘙𝘚𝘛𝘜𝘝𝘞𝘟𝘠𝘡";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), italic.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createSansSerifMap() {
        Map<Character, Character> map = new HashMap<>();
        String sansSerif = "𝗮𝗯𝗰𝗱𝗲𝗳𝗴𝗵𝗶𝗷𝗸𝗹𝗺𝗻𝗼𝗽𝗾𝗿𝘀𝘵𝘂𝘷𝘄𝘅𝘆𝘇" +
                "𝗔𝗕𝗖𝗗𝗘𝗙𝗚𝗛𝗜𝗝𝗞𝗟𝗠𝗡𝗢𝗣𝗤𝗥𝗦𝗧𝗨𝗩𝗪𝗫𝗬𝗭";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), sansSerif.charAt(i));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createRunicMap() {
        Map<Character, Character> map = new HashMap<>();
        // El runic es más corto, por eso usamos modulo:
        String runic = "ᚨᛒᛍᛞᛖᛜᚷᚻᛁᛃᚲᛚᛗᚾᛟᛈᛩᚱᛊᛏᚢᛥᚹᛉᛦᛎ";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), runic.charAt(i % runic.length()));
        }
        map.put(' ', ' ');
        return map;
    }

    private static Map<Character, Character> createArabicMap() {
        Map<Character, Character> map = new HashMap<>();
        // String arabic también corto, uso modulo para asignar:
        String arabic = "ﺍﺏﺝﺩﻩﻑﻕﻝﻡﻥﻭﺯﻉﻑﻙﻉﻥﺭﺱﺕﻭﺩﺹﺱﺵﺽﺷﻭﺡﺹﺫ";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), arabic.charAt(i % arabic.length()));
        }
        map.put(' ', ' ');
        return map;
    }
}
