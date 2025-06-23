package ex.nervisking.utils;

import ex.nervisking.ExApi;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class JSONMessage {

    private final Player player;
    private final UtilsManagers utilsManagers;
    private final TextComponent root;

    public JSONMessage(Player player) {
        this.player = player;
        this.utilsManagers = ExApi.getUtilsManagers();
        this.root = new TextComponent("");
    }

    // Clase interna fluida para construir componentes y agregarlos al root
    public class Part {

        private final String text;
        private BaseComponent[] hover;
        private ClickEvent clickEvent;

        public Part(String text) {
            this.text = utilsManagers.setPlaceholders(player, text);
        }

        public Part hover(String... lines) {
            hover = new BaseComponent[lines.length];
            for (int i = 0; i < lines.length; i++) {
                String line = utilsManagers.setPlaceholders(player, lines[i]);
                if (i != lines.length - 1) {
                    line += "\n";
                }
                hover[i] = new TextComponent(TextComponent.fromLegacyText(line));
            }
            return this;
        }

        public Part suggest(String command) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            return this;
        }

        public Part run(String command) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        public Part openUrl(String url) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            return this;
        }

        public Part copy(String text) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text);
            return this;
        }


        public JSONMessage append() {
            TextComponent message = new TextComponent(TextComponent.fromLegacyText(text));
            if (hover != null) {
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
            }
            if (clickEvent != null) {
                message.setClickEvent(clickEvent);
            }
            root.addExtra(message);
            return JSONMessage.this;
        }
    }

    public Part part(String text) {
        return new Part(text);
    }

    public JSONMessage add(String text) {
        root.addExtra(new TextComponent(TextComponent.fromLegacyText(utilsManagers.setPlaceholders(player, text))));
        return this;
    }

    public void send() {
        player.spigot().sendMessage(root);
    }

    public TextComponent build() {
        return root;
    }
}
