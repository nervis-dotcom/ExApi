package ex.nervisking.colors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class ColorTranslator {

    /** @deprecated */
    @Deprecated
    public static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static final Pattern HEX_PATTERN = Pattern.compile("(&#[0-9a-fA-F]{6})");

    public ColorTranslator() {
    }

    public static String translateColorCodes(@NotNull String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        String hexColored;
        while(matcher.find()) {
            hexColored = matcher.group(1).substring(1);
            matcher.appendReplacement(sb, "" + ChatColor.of(hexColored));
        }

        matcher.appendTail(sb);
        hexColored = sb.toString();
        return ChatColor.translateAlternateColorCodes('&', hexColored);
    }

    public static TextComponent translateColorCodesToTextComponent(@NotNull String text) {
        String colored = translateColorCodes(text);
        TextComponent base = new TextComponent();
        BaseComponent[] converted = TextComponent.fromLegacyText(colored);
        BaseComponent[] var4 = converted;
        int var5 = converted.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            BaseComponent comp = var4[var6];
            base.addExtra(comp);
        }

        return base;
    }
}
