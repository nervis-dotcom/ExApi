package ex.nervisking.utils;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.ExPl;
import ex.nervisking.ModelManager.Plugins;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.*;

public class PyfigletMessage {

    private final UtilsManagers utilsManagers;
    private boolean status;
    private String startColor;
    private String endColor;
    // Cambiamos a LinkedHashSet para mantener orden y evitar duplicados
    private final Set<String> pluginsSet;
    private final List<String> infos;
    private List<String> pyfiglet = List.of(
            "               %plugin%",
            ".------------------..------------------.",
            "| .--------------. || .--------------. |",
            "| |  _________   | || |  ____  ____  | |",
            "| | |_   ___  |  | || | |_  _||_  _| | |",
            "| |   | |_  \\_|  | || |   \\ \\  / /   | |      Se Ha %status%",
            "| |   |  _|  _   | || |    > `' <    | |      Version » %version%",
            "| |  _| |___/ |  | || |  _/ /'`\\ \\_  | |      Autor » %autor%",
            "| | |_________|  | || | |____||____| | |",
            "| |              | || |              | |",
            "| '--------------' || '--------------' |",
            "'------------------''------------------'",
            "        Server version » %server%"
    );

    public PyfigletMessage() {
        this.utilsManagers = ExApi.getUtilsManagers();
        this.status = true;
        this.startColor = "#FFFE00";
        this.endColor = "#FDFDFD";
        this.pluginsSet = new LinkedHashSet<>();
        this.infos = new ArrayList<>();
    }

    public PyfigletMessage setStatus(boolean value) {
        this.status = value;
        return this;
    }

    public PyfigletMessage setStartColor(String color) {
        this.startColor = color;
        return this;
    }

    public PyfigletMessage setEndColor(String color) {
        this.endColor = color;
        return this;
    }

    // Sobrecarga para Strings (varargs)
    public PyfigletMessage addPlugins(String... plugins) {
        Collections.addAll(this.pluginsSet, plugins);
        return this;
    }

    // Sobrecarga para ExPl (varargs)
    public PyfigletMessage addPlugins(ExPl... exPls) {
        for (ExPl exPl : exPls) {
            this.pluginsSet.add(exPl.getName());
        }
        return this;
    }

    // Sobrecarga para Plugins (varargs)
    public PyfigletMessage addPlugins(Plugins... pluginsArray) {
        for (Plugins plugin : pluginsArray) {
            this.pluginsSet.add(plugin.getName());
        }
        return this;
    }

    public PyfigletMessage clearPlugins() {
        this.pluginsSet.clear();
        return this;
    }

    public PyfigletMessage setPyfiglet(String... pyfiglet) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, pyfiglet);
        this.pyfiglet = list;
        return this;
    }

    public PyfigletMessage addInfo(String... infos) {
        Collections.addAll(this.infos, infos);
        return this;
    }

    public PyfigletMessage addInfo(List<String> infos) {
        this.infos.addAll(infos);
        return this;
    }


    public PyfigletMessage clearInfo() {
        this.infos.clear();
        return this;
    }

    public void build() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        for (String line : pyfiglet) {
            line = utilsManagers.applyGradient(line.replace("%server%", ExApi.getServerVersion())
                    .replace("%version%", ExApi.getPluginVersion())
                    .replace("%autor%", ExApi.getPluginAuthor())
                    .replace("%status%", status ? "Iniciado" : "Apagado")
                    .replace("%plugin%", ExApi.getPlugin().getName()), startColor, endColor);
            console.sendMessage(line);
        }

        if (!infos.isEmpty()) {
            utilsManagers.consoleMessage(" ");
            for (String info : infos) {
                utilsManagers.consoleMessage(info);
            }
        }

        if (!pluginsSet.isEmpty()) {
            utilsManagers.consoleMessage(" ");
            for (String name : pluginsSet) {
                utilsManagers.consoleMessage("&e‖ &f{0} &7» &7[{1}&7]".replace("{0}", name).replace("{1}", ExApi.isPlugin(name) ? "&a✓" : "&c✘"));
            }
        }
    }
}
