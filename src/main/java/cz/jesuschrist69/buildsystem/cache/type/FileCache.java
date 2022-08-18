package cz.jesuschrist69.buildsystem.cache.type;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.cache.LocalCache;
import cz.jesuschrist69.buildsystem.exceptions.BuildSystemException;
import cz.jesuschrist69.buildsystem.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FileCache extends LocalCache<String, YamlConfiguration> {

    /**
     * It loads all the files from the jar file and registers them
     *
     * @param instance The instance of the BuildSystem class.
     */
    public void init(@NotNull BuildSystem instance) {
        String jarPath = FileUtils.findJarPath(instance);
        if (jarPath != null) {
            List<String> fileNames = FileUtils.getFileListFromJar(instance);
            for (String file : fileNames) {
                register(file, FileUtils.loadFile(instance, file));
            }
        } else {
            throw new BuildSystemException("Failed to find jar file. Files could not be loaded!");
        }
    }

}
