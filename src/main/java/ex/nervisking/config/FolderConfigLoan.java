package ex.nervisking.config;

import ex.nervisking.ExApi;
import ex.nervisking.ModelManager.Configurate;
import ex.nervisking.ModelManager.Pattern.KeyAlphaNum;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class FolderConfigLoan<T extends JavaPlugin> {

    protected final T plugin;
    protected String folderName;
    protected ArrayList<CustomConfig> configFiles;

    @SuppressWarnings("unchecked")
    public FolderConfigLoan() {
        this.configFiles = new ArrayList<>();
        this.plugin = ExApi.getPluginOf((Class<T>) JavaPlugin.class);
        this.folderName = folderName();
        this.create();
        this.createFolder();
        this.registerConfigFiles();
        this.initialize();
        this.loadConfigs();
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
        configFiles.add(config);

        return config;
    }

    private void create() {
        for (Configurate files : createConfigFiles()) {
            CustomConfig.of(files.fileName(), files.folderName() == null ? folderName : files.folderName(), false);
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

    public void initialize() {}
    public abstract void loadConfigs();
    public abstract @KeyAlphaNum String folderName();
    public List<Configurate> createConfigFiles() {
        return List.of();
    }
}