package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Pattern.ToUse;
import ex.nervisking.ModelManager.Scheduler;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Clase para crear conversaciones interactivas con un jugador en el chat.
 * Permite configurar mensajes, validaciones, comportamiento de cancelación y opciones del sistema de conversación de Bukkit.
 */
public class PlayerPrompt {

    private final ConversationFactory factory;
    private final UtilsManagers utilsManagers;
    private final Player player;
    private final String promptMessage;

    private String cancelKeyword;
    private String prefix;
    private Runnable cancelCallback;
    private Predicate<String> inputValidator;
    private Consumer<String> inputCallback;

    private boolean modality;
    private boolean localEcho;

    public PlayerPrompt(Player player, String promptMessage) {
        this.factory = new ConversationFactory(ExApi.getPlugin());
        this.utilsManagers = ExApi.getUtilsManagers();
        this.player = player;
        this.promptMessage = promptMessage;

        this.cancelKeyword = "cancel";
        this.prefix = null;
        this.inputValidator = null;
        this.inputCallback = null;

        this.modality = true;
        this.localEcho = false;
    }

    public PlayerPrompt(Player player, String promptMessage, @NotNull Consumer<PlayerPrompt> action) {
        this(player, promptMessage);
        action.accept(this);
    }

    @Contract("_, _ -> new")
    public static @NotNull PlayerPrompt of(Player player, String promptMessage) {
        return new PlayerPrompt(player, promptMessage);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull PlayerPrompt of(Player player, String promptMessage, @NotNull Consumer<PlayerPrompt> action) {
        return new PlayerPrompt(player, promptMessage, action);
    }

    @ToUse(value = "Acción que se ejecuta cuando el jugador cancela la entrada")
    public PlayerPrompt onCancel(Runnable cancelCallback) {
        this.cancelCallback = cancelCallback;
        return this;
    }

    @ToUse(value = "Establece una función de validación para la entrada del jugador")
    public PlayerPrompt setValidator(Predicate<String> validator) {
        this.inputValidator = validator;
        return this;
    }

    @ToUse(value = "Acción que se ejecuta cuando la entrada es válida")
    public PlayerPrompt onInput(Consumer<String> inputCallback) {
        this.inputCallback = inputCallback;
        return this;
    }

    @ToUse(value = "Cambia la palabra clave para cancelar la entrada")
    public PlayerPrompt setCancelKeyword(String cancelKeyword) {
        this.cancelKeyword = cancelKeyword;
        return this;
    }

    @ToUse(value = "Establece un prefijo para los mensajes del prompt")
    public PlayerPrompt setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @ToUse(value = "Define si la conversación es modal (bloquea otros comandos y chat mientras está activa)")
    public PlayerPrompt setModality(boolean modality) {
        this.modality = modality;
        return this;
    }

    @ToUse(value = "Define si el chat local repite lo que escribe el jugador")
    public PlayerPrompt setLocalEcho(boolean localEcho) {
        this.localEcho = localEcho;
        return this;
    }

    @ToUse(value = "Envía la conversación al jugador")
    public void send() {
        factory.withModality(modality) // configurable
                .withLocalEcho(localEcho) // configurable
                .withPrefix(context -> prefix != null ? utilsManagers.setColoredMessage(prefix) : "")
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public @NotNull String getPromptText(@NotNull ConversationContext context) {
                        return utilsManagers.setColoredMessage(promptMessage);
                    }

                    @Override
                    public Prompt acceptInput(@NotNull ConversationContext context, String input) {
                        if (input.equalsIgnoreCase(cancelKeyword)) {
                            context.getForWhom().sendRawMessage(utilsManagers.setColoredMessage("&cEntrada cancelada."));
                            if (cancelCallback != null) {
                                Scheduler.run(cancelCallback);
                            }
                            return END_OF_CONVERSATION;
                        }

                        if (inputValidator == null || inputValidator.test(input)) {
                            if (inputCallback != null) {
                                Scheduler.run(() -> inputCallback.accept(input));
                            }
                            return END_OF_CONVERSATION;
                        }
                        return this; // re-prompt en caso de validación fallida
                    }
                })
                .withEscapeSequence(cancelKeyword)
                .thatExcludesNonPlayersWithMessage("¡Solo los jugadores pueden proporcionar aportes!")
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit() && cancelCallback != null) {
                        Scheduler.run(cancelCallback);
                    }
                });

        factory.buildConversation(player).begin();
    }
}