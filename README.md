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

---

## 📦 Instalación [![Version](https://img.shields.io/badge/version-1.0.3-green.svg)]

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
    public void Enable() {
        // Registrar comando
        ExAPI.getCommandManager().register(new FlyCommand());
        
        // Abrir menú de ejemplo
        ExAPI.getMenuManager().openMenu(player, new ExampleMenu());
        
        // Enviar mensaje
        ExAPI.getMessageManager().send(player, "plugin.loaded");
        
        // Configuración YAML/JSON
        MyConfig config = ExAPI.getConfigManager().load(MyConfig.class, "config.yml");
        config.reload(); // recarga dinámica
    }
}
```
---
