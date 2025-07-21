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

    public JSONMessage(Player player) {
        this.player = player;
        this.utilsManagers = ExApi.getUtilsManagers();
        this.root = new TextComponent("");
    }

    public JSONMessage() {
        this.player = null;
        this.utilsManagers = ExApi.getUtilsManagers();
        this.root = new TextComponent("");
    }

    public static JSONMessage of(Player player) {
        return new JSONMessage(player);
    }

    public static JSONMessage of() {
        return new JSONMessage();
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
            this.text = utilsManagers.setPlaceholders(player, text);
        }

        public Part(String text) {
            if (player == null) {
                this.text = utilsManagers.setColoredMessage(text);
            } else {
                this.text = utilsManagers.setPlaceholders(player, text);
            }
        }

        public Part hover(Player player, List<String> lines) {
            if (lines == null || lines.isEmpty()) {
                hover = null;
                return this;
            }

            hover = new BaseComponent[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                String line = utilsManagers.setPlaceholders(player, lines.get(i));
                if (i != lines.size() - 1) {
                    line += "\n";
                }
                hover[i] = new TextComponent(TextComponent.fromLegacyText(line));
            }
            return this;
        }

        public Part hover(List<String> lines) {
            if (lines == null || lines.isEmpty()) {
                hover = null;
                return this;
            }

            hover = new BaseComponent[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                String line;
                if (player == null) {
                    line = utilsManagers.setColoredMessage(lines.get(i));
                } else {
                    line = utilsManagers.setPlaceholders(player, lines.get(i));
                }
                if (i != lines.size() - 1) {
                    line += "\n";
                }
                hover[i] = new TextComponent(TextComponent.fromLegacyText(line));
            }
            return this;
        }

        public Part hover(Player player, String... lines) {
            if (lines == null || lines.length == 0) {
                hover = null;
                return this;
            }

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

        public Part hover(String... lines) {
            if (lines == null || lines.length == 0) {
                hover = null;
                return this;
            }

            hover = new BaseComponent[lines.length];
            for (int i = 0; i < lines.length; i++) {
                String line;
                if (player == null) {
                    line = utilsManagers.setColoredMessage(lines[i]);
                } else {
                    line = utilsManagers.setPlaceholders(player, lines[i]);
                }
                if (i != lines.length - 1) {
                    line += "\n";
                }
                hover[i] = new TextComponent(TextComponent.fromLegacyText(line));
            }
            return this;
        }

        public Part action(Player player, Action action, String value) {
            if (action == null || value == null || value.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Action or value cannot be null or empty.");
                return this;
            }

            value = utilsManagers.setPlaceholders(player, value);
            return switch (action) {
                case SUGGEST -> suggest(value);
                case EXECUTE -> run(value);
                case OPEN -> openUrl(value);
                case COPY -> copy(value);
                case NONE -> {
                    clickEvent = null;
                    utilsManagers.sendLogger(Logger.ERROR, "Invalid action specified: " + action);
                    yield this;
                }
            };
        }

        public Part action(Action action, String value) {
            if (action == null || value == null || value.isEmpty()) {
                utilsManagers.sendLogger(Logger.ERROR, "Action or value cannot be null or empty.");
                return this;
            }

            if (player != null) {
                value = utilsManagers.setPlaceholders(player, value);
            } else {
                value = utilsManagers.setColoredMessage(value);
            }
            return switch (action) {
                case SUGGEST -> suggest(value);
                case EXECUTE -> run(value);
                case OPEN -> openUrl(value);
                case COPY -> copy(value);
                case NONE -> {
                    clickEvent = null;
                    utilsManagers.sendLogger(Logger.ERROR, "Invalid action specified: " + action);
                    yield this;
                }
            };
        }

        private Part suggest(String command) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            return this;
        }

        private Part run(String command) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        private Part openUrl(String url) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            return this;
        }

        private Part copy(String text) {
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
        root.addExtra(new TextComponent(TextComponent.fromLegacyText(utilsManagers.setPlaceholders(player, text))));
        return this;
    }

    public JSONMessage add(String text) {
        if (player != null) {
            text = utilsManagers.setPlaceholders(player, text);
        } else {
            text = utilsManagers.setColoredMessage(text);
        }
        root.addExtra(new TextComponent(TextComponent.fromLegacyText(text)));
        return this;
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
