package net.pino.simpleconfig;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class BasicQuickConfig implements QuickConfig {

    private File configFile;

    private FileConfiguration fileConfiguration;

    @Override
    public File getConfigFile() {
        return configFile;
    }

    @Override
    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    @Override
    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    @Override
    public void setFileConfiguration(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }
}
