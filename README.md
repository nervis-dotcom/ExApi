📌 ExAPI – Librería para desarrollo de plugins en Minecraft

ExAPI es una librería ligera y modular que facilita la creación de plugins en Minecraft.
Incluye un sistema de menús, comandos, eventos, mensajes, configuración en YAML/JSON y múltiples utilidades que reducen código repetitivo y mejoran la organización.

✨ Características principales

📂 Sistema de menús: creación de menús interactivos con soporte para acciones personalizadas.

⌨️ Comandos: registro y gestión simplificada de comandos.

🎯 Eventos: sistema de eventos optimizado y extensible.

💬 Mensajes: envío de mensajes con soporte para placeholders y formatos configurables.

🗄️ Configuraciones: soporte para archivos YAML y JSON, con carga, guardado y recarga automática.

📦 Instalación

Agrega el repositorio y la dependencia en tu proyecto:

<repositories>
    <repository>
        <id>exapi-repo</id>
        <url>https://github.com/NervisKing/ExAPI/packages</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ex.nervisking</groupId>
        <artifactId>ExAPI</artifactId>
        <version>1.0.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

🚀 Ejemplo de uso

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Registro de comandos
        ExAPI.getCommandManager().register(new FlyCommand());
        
        // Sistema de menús
        ExAPI.getMenuManager().openMenu(player, new ExampleMenu());
        
        // Mensajes
        ExAPI.getMessageManager().send(player, "plugin.loaded");
        
        // Configuración YAML/JSON
        MyConfig config = ExAPI.getConfigManager().load(MyConfig.class, "config.yml");
        config.reload(); // recarga dinámica
    }
}

