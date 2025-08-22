# 📌 ExAPI – Librería para desarrollo de plugins en Minecraft  
[![License](https://img.shields.io/badge/License-Usage--Only-blue.svg)](./LICENSE)

**ExAPI** es una librería ligera y modular que facilita la creación de plugins en Minecraft.  
Incluye un sistema de menús, comandos, eventos, mensajes, configuración en **YAML/JSON** y múltiples utilidades que reducen código repetitivo y mejoran la organización.

---

## ✨ Características principales

- 📂 **Sistema de menús**: creación de menús interactivos con soporte para acciones personalizadas.  
- ⌨️ **Comandos**: registro y gestión simplificada de comandos.  
- 🎯 **Eventos**: sistema de eventos optimizado y extensible.  
- 💬 **Mensajes**: envío de mensajes con soporte para placeholders y formatos configurables.  
- 🗄️ **Configuraciones**: soporte para archivos YAML y JSON, con carga, guardado y recarga automática.  
 🎨 **Colores**: soporte para **Legacy (&c)**, **Hex (#ff0000) o (&#ff0000)** y **MiniMessage** (`<red>texto</red>`).  

---

## 📦 Instalación [![Version](https://img.shields.io/badge/version-1.0.5-green.svg)](https://github.com/nervis-dotcom/ExApi/tree/main)

Agrega el repositorio y la dependencia en tu proyecto:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.github.nervis-dotcom</groupId>
        <artifactId>ExApi</artifactId>
        <version>VERSION</version>
        <scope>compile</scope>
     </dependency>
</dependencies>
```
---
## 🚀 Ejemplo de uso

```java
public class MyPlugin extends ExPlugin {

    @Override
    public void Load() {

    }

    @Override
    public void Enable() {
        // Registrar comando
        this.commandManager.registerCommand(new WeatherCommand());

        // Registra Evento
        this.eventsManager.registerEvents(new JoinListener());
    }

    @Override
    protected boolean Menu() {
        return true; // Enable menu system
    }

    @Override
    protected void Disable() {

    }
}
```
---

# 📂 Ejemplos

## ⌨️ Comando

```java
public class WeatherCommand extends Command {

    @Override
    public @KeyDef String getName() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean getPermission() {
        return true;
    }

    @Override
    public void onCommand(Sender sender, Arguments args) {
        if (sender.isConsole()) {
            noConsole(sender);
            return;
        }

        if (!hasPermission(sender)) {
            noPermission(sender);
            return;
        }

        if (args.lacksMinArgs(1)) {
            help(sender, "&eUso: &7/weather [rain | thunder | clear] [mundo opcional]");
            return;
        }

        World world;
        if (args.has(2)) {
            world = Bukkit.getWorld(args.get(1));
            if (world == null) {
                sendMessage(sender, "%prefix% &cEl mundo especificado no existe.");
                return;
            }
        } else {
            world = sender.getWorld();
        }

        switch (args.get(0).toLowerCase()) {
            case "rain":
                world.setStorm(true);
                world.setThundering(false);
                sendMessage(sender, "%prefix% &bEl clima en " + world.getName() + " ha sido cambiado a lluvia.");
                break;
            case "thunder":
                world.setStorm(true);
                world.setThundering(true);
                sendMessage(sender, "%prefix% &bEl clima en " + world.getName() + " ha sido cambiado a tormenta.");
                break;
            case "clear":
                world.setStorm(false);
                world.setThundering(false);
                sendMessage(sender, "%prefix% &bEl clima en " + world.getName() + " ha sido cambiado a despejado.");
                break;
            default:
                help(sender, "&eUso: &7/weather [rain | thunder | clear] [mundo opcional]");
                break;
        }
    }

    @Override
    public Completions onTab(Sender sender, Arguments args, Completions completions) {
        if (!hasPermission(sender)) {
            return completions;
        }

        if (args.has(1)) {
            completions.add("rain", "thunder", "clear");
        } else if (args.has(2)) {
            completions.add(Bukkit.getWorlds().stream().map(World::getName).toList());
        }
        return completions;
    }
}
```
---

## 🎯 Evento

```java
public class JoinListener extends Event<MyPlugin> {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sendMessage(player, "<green>¡Bienvenido a ExAPI!</green>");
    }
}

```
## Config

- Yml

```java
public class MainConfigYml {

    private final CustomConfig configFile;
    private String prefix;

    public MainConfig(){
        this.configFile = CustomConfig.of("ConfigYml");
        this.configFile.registerConfig();
        this.loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        prefix = config.getString("prefix", "ex");
    }

    public String getPrefix() {
        return prefix;
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }
}

```

- Json

```java
public class MainConfigJson {

    private final JsonConfig jsonConfig;
    private String prefix;

    public ConfigLicense() {
        this.jsonConfig = JsonConfig.of("ConfigJson");
        this.loadConfig();
    }

    private void loadConfig() {
        prefix = jsonConfig.getString("prefix", "ex");
    }

    public String getPrefix() {
        return prefix;
    }

    public void reloadConfig(){
        jsonConfig.reload();
        loadConfig();
    }
}

```
