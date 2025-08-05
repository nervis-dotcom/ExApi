package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("deprecation")
public class JSONMessage {

    private final Player player;
    private final UtilsManagers utilsManagers;
    private final TextComponent root;

    public JSONMessage(Player player, String message) {
        this.player = player;
        this.utilsManagers = ExApi.getUtilsManagers();
        this.root = new TextComponent(message);
    }

    public JSONMessage(Player player) {
        this(player, "");
    }

    public JSONMessage(String message) {
        this(null, message);
    }

    public JSONMessage() {
        this(null, "");
    }

    public static JSONMessage of(Player player) {
        return new JSONMessage(player);
    }

    public static JSONMessage of() {
        return new JSONMessage();
    }

    public static JSONMessage of(Player player, String message) {
        return new JSONMessage(player, message);
    }

    public static JSONMessage of(String message) {
        return new JSONMessage(message);
    }

    /**
     * Enum para definir las acciones que se pueden realizar con los mensajes JSON.
     * Cada acción corresponde a un tipo de evento de clic en el mensaje.
     */
    public enum Action {

        NONE,
        SUGGEST,
        EXECUTE,
        OPEN,
        COPY;

        public static Action fromString(String action) {
            for (Action a : values()) {
                if (a.toString().equalsIgnoreCase(action)) {
                    return a;
                }
            }
            return NONE;
        }
    }

    /**
     * Clase interna para representar una parte del mensaje JSON.
     * Permite definir el texto, eventos de clic y eventos de hover.
     */
    public class Part {

        private final String text;
        private BaseComponent[] hover;
        private ClickEvent clickEvent;

        public Part(Player player, String text) {
            this.text = player == null ? utilsManagers.setColoredMessage(text) : utilsManagers.setPlaceholders(player, text);
        }

        public Part(String text) {
            this(player, text);
        }

        public Part hover(List<String> lines) {
            return hover(lines != null ? lines.toArray(new String[0]) : null);
        }

        public Part hover(Player player, List<String> lines) {
            return hover(player, lines != null ? lines.toArray(new String[0]) : null);
        }

        public Part hover(String... lines) {
            return this.hover(player, lines);
        }

        public Part hover(Player player, String... lines) {
            if (lines == null || lines.length == 0) {
                hover = null;
                return this;
            }

            hover = new BaseComponent[lines.length];
            for (int i = 0; i < lines.length; i++) {
                String line = player == null ? utilsManagers.setColoredMessage(text) : utilsManagers.setPlaceholders(player, text);
                if (i != lines.length - 1) {
                    line += "\n";
                }
                hover[i] = new TextComponent(TextComponent.fromLegacyText(line));
            }
            return this;
        }

        public Part action(Action action, String value) {
           return this.action(player, action, value);
        }

        public Part action(Player player, Action action, String value) {
            if (action == null || value == null || value.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Action or value cannot be null or empty.");
                return this;
            }
            String parsedValue = player != null ? utilsManagers.setPlaceholders(player, value) : utilsManagers.setColoredMessage(value);
            return switch (action) {
                case SUGGEST -> suggest(parsedValue);
                case EXECUTE -> run(parsedValue);
                case OPEN -> openUrl(parsedValue);
                case COPY -> copy(parsedValue);
                case NONE -> {
                    clickEvent = null;
                    utilsManagers.sendLogger(Logger.ERROR, "Invalid action specified: " + action);
                    yield this;
                }
            };
        }

        private Part suggest(String command) {
            if (command == null || command.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Command cannot be null or empty for suggest action.");
                return this;
            }
            this.clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            return this;
        }

        private Part run(String command) {
            if (command == null || command.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Command cannot be null or empty for suggest action.");
                return this;
            }
            this.clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        private Part openUrl(String url) {
            if (url == null || url.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Command cannot be null or empty for suggest action.");
                return this;
            }
            this.clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            return this;
        }

        private Part copy(String text) {
            if (text == null || text.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Command cannot be null or empty for suggest action.");
                return this;
            }
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

    public Part part(Player player, String text) {
        return new Part(player, text);
    }

    public Part part(String text) {
        return new Part(text);
    }

    public JSONMessage add(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return this;
        }

        text =  player != null ? utilsManagers.setPlaceholders(player, text) : utilsManagers.setColoredMessage(text);
        root.addExtra(new TextComponent(TextComponent.fromLegacyText(text)));
        return this;
    }

    public JSONMessage add(String text) {
        return this.add(player, text);
    }

    public JSONMessage addLine(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return this;
        }
        return add(player, "\n" + text);
    }

    public JSONMessage addLine(String text) {
        return addLine(player, text);
    }

    public void send() {
        if (root.getExtra() == null || root.getExtra().isEmpty()) {
            return;
        }
        if (player != null) {
            player.spigot().sendMessage(root);
        } else {
            utilsManagers.sendLogger(Logger.ERROR, "Cannot send message: Player is null.");
        }
    }

    public TextComponent build() {
        return root;
    }
}
