package ex.nervisking.License;

import ex.nervisking.ModelManager.CustomColor;
import ex.nervisking.utils.DiscordWebhooks;
import ex.nervisking.utils.PyfigletMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LicenseManager {

    public static boolean validateLicense(String key, JavaPlugin javaPlugin, PyfigletMessage pyfigletMessage) {
        DiscordWebhooks webhook = new DiscordWebhooks("https://discord.com/api/webhooks/1391625588434931782/u2fy3MM8N3r9FJzqGiG9kIYTikoNbwVXHHgZg1gUb9odQ40ns92_Eg8J4bs_I65M_wd3")
                .setBotName("Ex")
                .setAvatar("https://media.discordapp.net/attachments/1244423134023520331/1391627154004906136/OIG1._M_9HWevC5FC.jpg?ex=686c9548&is=686b43c8&hm=63766a523215cfad4bc70fce25d0cf29bb23d1090afdfe1b8deca7cc62ebd50a&=&format=webp&width=666&height=666")
                .setTitle("License");
        boolean status = false;
        License license = new License();
        try {
            // URL url = new URL("http://216.173.77.230:25578/validate?license=" + key);
            URL url = new URL("http://key.neovex.net:25579/validate?license=" + key);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            BufferedReader in;

            // ✅ Usar getErrorStream si el código no es 200
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            StringBuilder res = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) res.append(line);
            in.close();

            String json = res.toString();
            boolean valid = json.contains("\"valid\":true");
            String plugin = extractJsonValue(json, "plugin");
            boolean pl = plugin.equalsIgnoreCase(javaPlugin.getName());

            String user = extractJsonValue(json, "user");
            String registeredIp = extractJsonValue(json, "registered_ip");
            String ip = extractJsonValue(json, "ip");
            String message = extractJsonValue(json, "message");

            license.setPlugin(plugin);
            license.setUser(user);
            license.setRegisteredIp(registeredIp);
            license.setIp(ip);
            license.setMessage(message);

            status = (valid && pl);
        } catch (Exception e) {

            if (e instanceof ConnectException) {
                license.setMessage("&e‖ &cNo se pudo conectar al servidor de licencias.");
            } else {
                license.setMessage("⚠️ Error al validar licencia:");
                e.printStackTrace();
            }
        }

        if (!status) {
            webhook.setDescription("``" + license.getMessage() + "``")
                    .setColor(CustomColor.RED)
                    .setTimestamp()
                    .addField("**Licencia**", "> ||``" + key + "``||", false)
                    .addField("**Plugin**", "> ```" + javaPlugin.getName() + "```", false)
                    .addField("**Usuario**", "> ||```" + license.getUser() + "```||", false)
                    .addField("**ip Registrada**", "> ||```" + license.getRegisteredIp() + "```||", false)
                    .addField("**ip**", "> ||```" + license.getIp() + "```||", false)
                    .build();
            pyfigletMessage.setStatus(false).setClear(true).addInfo(license.getMessage(), "&e‖ &cLicencia: &f'" + key + "'");
        } else {
            pyfigletMessage.addInfo(license.getMessage(), "&e‖ &aLicencia: &m&0'" + key + "'");
            webhook.setDescription("``" + license.getMessage() + "``")
                    .setColor(CustomColor.GREEN)
                    .setTimestamp()
                    .addField("**Licencia**", "> ||``" + key + "``||", false)
                    .addField("**Plugin**", "> ```" + license.getPlugin() + "```", false)
                    .addField("**Usuario**", "> ||```" + license.getUser() + "```||", false)
                    .addField("**ip Registrada**", "> ||```" + license.getRegisteredIp() + "```||", false)
                    .addField("**ip**", "> ||```" + license.getIp() + "```||", false)
                    .buildAsync();
        }

        return status;
    }

    private static String extractJsonValue(String json, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search);
            if (start == -1) return "undefined";
            start += search.length();
            int end = json.indexOf("\"", start);
            if (end == -1) return "undefined";
            return json.substring(start, end);
        } catch (Exception e) {
            return "undefined";
        }
    }


    private static class License {

        private String plugin;
        private String user;
        private String registeredIp;
        private String ip;
        private String message;

        public License() {
            this.plugin = "undefined";
            this.user = "undefined";
            this.registeredIp = "undefined";
            this.ip = "undefined";
            this.message = "Sin mensaje";
        }

        public String getPlugin() {
            return plugin;
        }

        public void setPlugin(String plugin) {
            this.plugin = plugin;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getRegisteredIp() {
            return registeredIp;
        }

        public void setRegisteredIp(String registeredIp) {
            this.registeredIp = registeredIp;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}