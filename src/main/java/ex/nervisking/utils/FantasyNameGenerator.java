package ex.nervisking.utils;

import java.util.Random;

public class FantasyNameGenerator {

    private static final String[] PREFIXES = {
            "Ar", "Bel", "Dor", "El", "Fa", "Gal", "Hal", "Is", "Jar", "Kel", "Lor", "Mor", "Nor", "Or", "Per", "Quor", "Ral", "Sar", "Tor", "Ur", "Vor", "Wyn", "Xan", "Yor", "Zel"
    };

    private static final String[] MIDDLES = {
            "ael", "bor", "cer", "dan", "eth", "fal", "gorn", "hild", "ion", "jor", "kas", "len", "mir", "nys", "or", "phir", "quil", "ras", "sil", "tur", "und", "var", "wyn", "xil", "yas", "zor"
    };

    private static final String[] SUFFIXES = {
            "dor", "ion", "ar", "mir", "iel", "or", "as", "ur", "en", "il", "ys", "eth", "el", "us", "an", "ir", "al", "on", "ax", "or"
    };

    private static final Random RANDOM = new Random();

    public static String generateName() {
        String prefix = PREFIXES[RANDOM.nextInt(PREFIXES.length)];
        String middle = MIDDLES[RANDOM.nextInt(MIDDLES.length)];
        String suffix = SUFFIXES[RANDOM.nextInt(SUFFIXES.length)];
        return prefix + middle + suffix;
    }

}
