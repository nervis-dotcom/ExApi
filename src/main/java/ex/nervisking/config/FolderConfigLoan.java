package ex.nervisking.config;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Configurate;
import ex.nervisking.ModelManager.Pattern.KeyLet;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class FolderConfigLoan {

    protected JavaPlugin plugin;
    protected @KeyLet String folderName;
    protected ArrayList<CustomConfig> configFiles;

    public FolderConfigLoan() {
        this.configFiles = new ArrayList<>();
        this.plugin = ExApi.getPlugin();
        this.folderName = folderName();
        this.create();
        this.createFolder();
        this.registerConfigFiles();
    }

    public void reloadConfigs() {
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

    public CustomConfig registerConfigFile(String pathName) {
        CustomConfig config = new CustomConfig(pathName, folderName, true);
        config.registerConfig();
        configFiles.add(config);

        return config;
    }

    private void create() {
        for (Configurate files : createConfigFiles()) {
            CustomConfig config = new CustomConfig(files.fileName(), files.folderName() == null ? folderName : files.folderName(), false);
            config.registerConfig();
        }
    }

    public boolean removeFile(String config) {
        CustomConfig configFile = getConfigFile(config + ".yml");
        if (configFile == null) {
            return false;
        }

        File file = new File(plugin.getDataFolder() + File.separator + folderName, configFile.getPath());
        removeConfig(configFile.getPath());
        return file.delete();
    }

    private void removeConfig(String path) {
        configFiles.removeIf(cfg -> cfg.getPath().equals(path));
    }

    public abstract void loadConfigs();
    public abstract @KeyLet String folderName();
    public List<Configurate> createConfigFiles() {
        return List.of();
    }
}