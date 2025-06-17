package ex.nervisking.config;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Configurate;
import ex.nervisking.ModelManager.Logger;
import ex.nervisking.ModelManager.Scheduler;
import ex.nervisking.ModelManager.Task;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DataFolderConfigManager {

    protected JavaPlugin plugin;
    protected String folderName;
    protected ArrayList<CustomConfig> configFiles;
    private Task task;

    public DataFolderConfigManager() {
        this.configFiles = new ArrayList<>();
        this.plugin = ExApi.getPlugin();
        this.folderName = folderName();
        this.create();
        this.createFolder();
        this.registerConfigFiles();
        this.getUpdate();
    }

    private void getUpdate() {
        if (autoSave()) {
            if (time() <= 0) {
                ExApi.getUtilsManagers().sendLogger(Logger.WARNING, "&cEl tiempo de auto guardado debe ser mayor a 0.");
                return;
            }
            this.task = Scheduler.runTimer(() -> {
                this.saveConfigs();
                if (message()) {
                    ExApi.getUtilsManagers().sendLogger(Logger.INFO, "&aGuardando data &e" + configFiles.size() + " &aArchivos en &b" + folderName + "...");
                }
            }, 10 * 60 * 20, time() * 60 * 20L);

        }
    }

    public void reloadConfigs() {
        if (task != null) {
            task.cancel();
            this.getUpdate();
        }
        this.configFiles.clear();
        this.registerConfigFiles();
        this.loadConfigs();
    }

    private void createFolder() {
        File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void saveConfigFiles() {
        for (CustomConfig configFile : configFiles) {
            configFile.saveConfig();
        }
    }

    private void registerConfigFiles() {
        String path = plugin.getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : Objects.requireNonNull(listOfFiles)) {
            if (file.isFile()) {
                registerConfigFile(file.getName());
            }
        }
    }

    public ArrayList<CustomConfig> getConfigs() {
        return this.configFiles;
    }

    public CustomConfig getConfigFile(String pathName) {
        for (CustomConfig configFile : configFiles) {
            if (configFile.getPath().equals(pathName)) {
                return configFile;
            }
        }
        return null;
    }

    public CustomConfig registerConfigFile(String pathName) {
        CustomConfig config = new CustomConfig(pathName, folderName, plugin, true);
        config.registerConfig();
        configFiles.add(config);

        return config;
    }

    private void create() {
        for (Configurate files : createConfigFiles()) {
            CustomConfig configFile = new CustomConfig(files.fileName(), files.folderName(), plugin, false);
            configFile.registerConfig();
        }
    }

    public abstract void loadConfigs();
    public abstract void saveConfigs();
    public abstract String folderName();
    public int time() {
        return 0;
    }
    public boolean autoSave() {
        return false;
    }
    public boolean message() {
        return false;
    }
    public List<Configurate> createConfigFiles() {
        return List.of();
    }
}
