package ex.nervisking.utils;

import ex.nervisking.ExApi;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("deprecation")
public class JSONMessage {

    private final Player player;
    private final String text;
    private BaseComponent[] hover;
    private String suggestCommand;
    private String executeCommand;
    private final UtilsManagers utilsManagers;

    public JSONMessage(Player player, String text) {
        this.player = player;
        this.hover = null;
        this.text = text;
        this.utilsManagers = ExApi.getUtilsManagers();
    }

    public JSONMessage hover(List<String> list) {
        hover = new BaseComponent[list.size()];
        for (int i = 0; i < list.size(); i++) {
            TextComponent line;
            if (i == list.size() - 1) {
                line = new TextComponent(TextComponent.fromLegacyText(utilsManagers.setPlaceholders(player, list.get(i))));
            } else {
                line = new TextComponent(TextComponent.fromLegacyText(utilsManagers.setPlaceholders(player, list.get(i)) + "\n"));
            }
            hover[i] = line;
        }
        return this;
    }

    public JSONMessage setSuggestCommand(String command) {
        this.suggestCommand = command;
        return this;
    }

    public JSONMessage setExecuteCommand(String command) {
        this.executeCommand = command;
        return this;
    }

    public void send() {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(utilsManagers.setPlaceholders(player, text)));
        if (hover != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        }
        if (suggestCommand != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestCommand));
        }
        if (executeCommand != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, executeCommand));
        }
        player.spigot().sendMessage(message);
    }

    public TextComponent build() {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(utilsManagers.setPlaceholders(player, text)));
        if (hover != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        }
        if (suggestCommand != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestCommand));
        }
        if (executeCommand != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, executeCommand));
        }
        return message;
    }
}
