package ex.nervisking.config;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Logger;
import ex.nervisking.ModelManager.Scheduler;
import ex.nervisking.ModelManager.Task;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class CustomConfigSave {

    protected final JavaPlugin plugin;
    protected final String fileName;
    protected FileConfiguration config = null;
    protected final String folderName;
    private File file = null;
    private Task task;

    public CustomConfigSave() {
        this.plugin = ExApi.getPlugin();
        this.fileName = fileName();
        this.folderName = folderName();

        if (folderName != null) {
            file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
        } else {
            file = new File(plugin.getDataFolder(), fileName);
        }

        if (!file.exists()) {
            if (newFile()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (folderName != null) {
                    plugin.saveResource(folderName + File.separator + fileName, false);
                } else {
                    plugin.saveResource(fileName, false);
                }
            }

        }

        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        this.getUpdate();
    }

    private void getUpdate() {
        if (autoSave()) {
            if (setTime() <= 0) {
                ExApi.getUtilsManagers().sendLogger(Logger.WARNING, "&cEl tiempo de auto guardado no puede ser menor o igual a 0, se desactivara el auto guardado del archivo: &e" + fileName);
                return;
            }
            this.task = Scheduler.runTimer(() -> {
                this.saveData();
                if (message()) {
                    ExApi.getUtilsManagers().sendLogger(Logger.INFO, "&aGuardando data Archivo: &e" + fileName + "...");
                }
            }, 10 * 60 * 20, setTime() * 60 * 20L);
        }
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            if (folderName != null) {
                file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                file = new File(plugin.getDataFolder(), fileName);
            }

        }
        config = YamlConfiguration.loadConfiguration(file);

        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            config.setDefaults(defConfig);
        }

        return config;
    }

    public void reloadConfig() {
        if (config == null) {
            if (folderName != null) {
                file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                file = new File(plugin.getDataFolder(), fileName);
            }

        }
        config = YamlConfiguration.loadConfiguration(file);

        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            config.setDefaults(defConfig);
        }
        this.loadConfigs();
        if (task != null) {
            task.cancel();
            this.getUpdate();
        }
    }

    public abstract void loadConfigs();
    public abstract void saveData();
    public abstract String fileName();
    public abstract String folderName();
    public abstract boolean newFile();
    public int setTime() {
        return 0;
    }
    public boolean autoSave() {
        return false;
    }
    public boolean message() {
        return false;
    }
    public String getPath() {
        return this.fileName;
    }
}
