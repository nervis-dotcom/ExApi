package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.ColorUtil;
import ex.nervisking.ModelManager.CustomColor;
import ex.nervisking.ModelManager.Logger;
import ex.nervisking.ModelManager.Scheduler;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebhooks {

    private final String discordHook;
    private final UtilsManagers utilsManagers;

    private final StringBuilder rootBuilder;
    private final StringBuilder embedBuilder;
    private final StringBuilder contentBuilder;
    private final List<Field> fields;

    public DiscordWebhooks(String discordHook) {
        this.discordHook = discordHook;
        this.utilsManagers = ExApi.getUtilsManagers();
        this.rootBuilder = new StringBuilder();
        this.embedBuilder = new StringBuilder();
        this.contentBuilder = new StringBuilder();
        this.fields = new ArrayList<>();
    }

    public DiscordWebhooks setBotName(String name) {
        if (rootBuilder.length() > 0) rootBuilder.append(",");
        rootBuilder.append("\"username\": \"").append(escapeJson(name)).append("\"");
        return this;
    }

    public DiscordWebhooks setAvatarByPlayer(String username) {
        if (rootBuilder.length() > 0) rootBuilder.append(",");
        rootBuilder.append("\"avatar_url\": \"").append(escapeJson("https://mc-heads.net/avatar/" + username)).append("\"");
        return this;
    }

    public DiscordWebhooks setAvatar(String url) {
        if (!url.startsWith("http")) {
            utilsManagers.sendLogger(Logger.WARNING, "Avatar URL inválido: " + url);
            return this;
        }
        if (rootBuilder.length() > 0) rootBuilder.append(",");
        rootBuilder.append("\"avatar_url\": \"").append(escapeJson(url)).append("\"");
        return this;
    }


    public DiscordWebhooks setTitle(String title) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"title\": \"").append(escapeJson(limit(title, 256))).append("\"");
        return this;
    }

    public DiscordWebhooks setDescription(List<String> descriptionLines) {
        String joined = String.join("\n", descriptionLines);
        String colored = utilsManagers.removeColorCodes(joined);
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"description\": \"").append(escapeJson(colored)).append("\"");
        return this;
    }

    public DiscordWebhooks setDescription(String... descriptionLines) {
        String joined = String.join("\n", descriptionLines);
        String colored = utilsManagers.removeColorCodes(joined);
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"description\": \"").append(escapeJson(colored)).append("\"");
        return this;
    }

    public DiscordWebhooks setColor(int color) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"color\": ").append(color);
        return this;
    }

    public DiscordWebhooks setColor(CustomColor customColor) {
        if (customColor != null && customColor.getColor() != null) {
            Color c = customColor.getColor();
            int rgb = (c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue();
            return setColor(rgb);
        }
        return this;
    }

    public DiscordWebhooks setColor(String colorInput) {
        Color color = ColorUtil.parse(colorInput);
        if (color == null) return this;
        int rgb = (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue();
        return setColor(rgb);
    }

    public DiscordWebhooks setFooter(String text, String iconUrl) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"footer\": {")
                .append("\"text\": \"").append(escapeJson(text)).append("\",")
                .append("\"icon_url\": \"").append(escapeJson(iconUrl)).append("\"")
                .append("}");
        return this;
    }

    public DiscordWebhooks setThumbnailByPlayer(String username) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"thumbnail\": {")
                .append("\"url\": \"").append(escapeJson("https://mc-heads.net/avatar/" + username)).append("\"")
                .append("}");
        return this;
    }

    public DiscordWebhooks setThumbnail(String url) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"thumbnail\": {")
                .append("\"url\": \"").append(escapeJson(url)).append("\"")
                .append("}");
        return this;
    }

    public DiscordWebhooks setTimestamp(Instant instant) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"timestamp\": \"").append(escapeJson(instant.toString())).append("\"");
        return this;
    }

    public DiscordWebhooks setTimestamp() {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"timestamp\": \"").append(escapeJson(Instant.now().toString())).append("\"");
        return this;
    }

    public DiscordWebhooks setTimestamp(boolean value) {
        if (value) {
            if (embedBuilder.length() > 0) embedBuilder.append(",");
            embedBuilder.append("\"timestamp\": \"").append(escapeJson(Instant.now().toString())).append("\"");
        }
        return this;
    }

    public DiscordWebhooks addField(String name, String value, boolean inline) {
        fields.add(new Field(name, value, inline));
        return this;
    }

    public DiscordWebhooks setAuthor(String name, String url, String iconUrl) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"author\": {")
                .append("\"name\": \"").append(escapeJson(name)).append("\",")
                .append("\"url\": \"").append(escapeJson(url)).append("\",")
                .append("\"icon_url\": \"").append(escapeJson(iconUrl)).append("\"")
                .append("}");
        return this;
    }

    public DiscordWebhooks setImage(String url) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"image\": {")
                .append("\"url\": \"").append(escapeJson(url)).append("\"")
                .append("}");
        return this;
    }

    public DiscordWebhooks setUrl(String url) {
        if (embedBuilder.length() > 0) embedBuilder.append(",");
        embedBuilder.append("\"url\": \"").append(escapeJson(url)).append("\"");
        return this;
    }

    public DiscordWebhooks setContent(String... descriptionLines) {
        String joined = String.join("\n", descriptionLines);
        String colored = utilsManagers.removeColorCodes(joined);
        contentBuilder.setLength(0);
        contentBuilder.append("\"content\": \"").append(escapeJson(colored)).append("\"");
        return this;
    }

    public DiscordWebhooks clear() {
        rootBuilder.setLength(0);
        embedBuilder.setLength(0);
        contentBuilder.setLength(0);
        fields.clear();
        return this;
    }

    public void build() {
        HttpURLConnection connection = null;
        OutputStream os = null;

        try {
            URL url = new URL(discordHook);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Validación de fields
            List<Field> limitedFields = fields.size() > 25 ? fields.subList(0, 25) : fields;

            StringBuilder jsonPayload = new StringBuilder();
            jsonPayload.append("{");

            if (rootBuilder.length() > 0) {
                jsonPayload.append(rootBuilder).append(",");
            }

            if (contentBuilder.length() > 0) {
                jsonPayload.append(contentBuilder).append(",");
            }

            jsonPayload.append("\"embeds\": [{");

            if (embedBuilder.length() > 0) {
                jsonPayload.append(embedBuilder).append(",");
            }

            // Fields
            if (!limitedFields.isEmpty()) {
                jsonPayload.append("\"fields\": [");
                for (int i = 0; i < limitedFields.size(); i++) {
                    Field f = limitedFields.get(i);
                    jsonPayload.append("{")
                            .append("\"name\": \"").append(escapeJson(limit(f.name(), 256))).append("\",")
                            .append("\"value\": \"").append(escapeJson(limit(f.value(), 1024))).append("\",")
                            .append("\"inline\": ").append(f.inline())
                            .append("}");
                    if (i < limitedFields.size() - 1) {
                        jsonPayload.append(",");
                    }
                }
                jsonPayload.append("]");
            }

            jsonPayload.append("}]}");

            byte[] input = jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
            os = connection.getOutputStream();
            os.write(input, 0, input.length);

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_NO_CONTENT && code != HttpURLConnection.HTTP_OK) {
                utilsManagers.sendLogger(Logger.ERROR, "Failed to send message. HTTP error code: " + code);
            }

        } catch (MalformedURLException e) {
            utilsManagers.sendLogger(Logger.ERROR, "URL malformada: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            utilsManagers.sendLogger(Logger.ERROR, "Error de entrada/salida: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            utilsManagers.sendLogger(Logger.ERROR, "Error desconocido: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    utilsManagers.sendLogger(Logger.ERROR, "Error al cerrar OutputStream: " + e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void buildAsync() {
        Scheduler.runAsync(() -> {
            HttpURLConnection connection = null;
            OutputStream os = null;

            try {
                URL url = new URL(discordHook);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Validación de fields
                List<Field> limitedFields = fields.size() > 25 ? fields.subList(0, 25) : fields;

                StringBuilder jsonPayload = new StringBuilder();
                jsonPayload.append("{");

                if (rootBuilder.length() > 0) {
                    jsonPayload.append(rootBuilder).append(",");
                }

                if (contentBuilder.length() > 0) {
                    jsonPayload.append(contentBuilder).append(",");
                }

                jsonPayload.append("\"embeds\": [{");

                if (embedBuilder.length() > 0) {
                    jsonPayload.append(embedBuilder).append(",");
                }

                // Fields
                if (!limitedFields.isEmpty()) {
                    jsonPayload.append("\"fields\": [");
                    for (int i = 0; i < limitedFields.size(); i++) {
                        Field f = limitedFields.get(i);
                        jsonPayload.append("{")
                                .append("\"name\": \"").append(escapeJson(limit(f.name(), 256))).append("\",")
                                .append("\"value\": \"").append(escapeJson(limit(f.value(), 1024))).append("\",")
                                .append("\"inline\": ").append(f.inline())
                                .append("}");
                        if (i < limitedFields.size() - 1) {
                            jsonPayload.append(",");
                        }
                    }
                    jsonPayload.append("]");
                }

                jsonPayload.append("}]}");

                byte[] input = jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
                os = connection.getOutputStream();
                os.write(input, 0, input.length);

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT && code != HttpURLConnection.HTTP_OK) {
                    utilsManagers.sendLogger(Logger.ERROR, "Failed to send message. HTTP error code: " + code);
                }

            } catch (MalformedURLException e) {
                utilsManagers.sendLogger(Logger.ERROR, "URL malformada: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                utilsManagers.sendLogger(Logger.ERROR, "Error de entrada/salida: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                utilsManagers.sendLogger(Logger.ERROR, "Error desconocido: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        utilsManagers.sendLogger(Logger.ERROR, "Error al cerrar OutputStream: " + e.getMessage());
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String limit(String input, int maxLength) {
        if (input == null) return "";
        return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }


    private String escapeJson(String message) {
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private record Field(String name, String value, boolean inline) { }
}
