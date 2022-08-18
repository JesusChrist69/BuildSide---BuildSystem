package cz.jesuschrist69.buildsystem.utils;

import com.google.common.io.ByteStreams;
import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.exceptions.BuildSystemException;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileUtils {


    private static final Pattern JAR_PATTERN = Pattern.compile("(?i)(.*BuildSystem.*.jar)");
    private static String jarPath;

    /**
     * It loads a file from the plugin's resources folder if it doesn't exist in the plugin's data folder
     *
     * @param instance {@link BuildSystem} The instance of plugin.
     * @param path     The path to the file.
     * @param fileName The name of the file you want to load.
     * @return {@link YamlConfiguration} A YamlConfiguration object
     */
    public YamlConfiguration loadFile(@NotNull BuildSystem instance, @NotNull String path, @NotNull String fileName) {
        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdirs();
        }
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream inputStream = instance.getResource(file.getName());
                     OutputStream outputStream = new FileOutputStream(file)) {
                    if (inputStream == null) {
                        throw new BuildSystemException("File {0} not found in resources", fileName);
                    }
                    ByteStreams.copy(inputStream, outputStream);
                }
            } catch (Exception e) {
                throw new BuildSystemException("Unable to load file {0}", fileName, e);
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Loads a file from the data folder of the plugin
     *
     * @param instance {@link BuildSystem} The instance of the plugin.
     * @param fileName The name of the file you want to load.
     * @return {@link YamlConfiguration} A YamlConfiguration object.
     */
    public YamlConfiguration loadFile(@NotNull BuildSystem instance, @NotNull String fileName) {
        return loadFile(instance, instance.getDataFolder().getAbsolutePath(), fileName);
    }

    /**
     * It loads all classes from the jar file of the BuildSystem instance and returns all classes that implement the given
     * interface
     *
     * @param instance       The instance of the BuildSystem class.
     * @param interfaceClass The interface that the classes must implement.
     * @return {@link Set} A set of classes that implement the given interface.
     */
    public Set<Class<?>> getClassesForInt(@NotNull BuildSystem instance, @NotNull Class<?> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new BuildSystemException("Listener interface must be an interface.");
        }
        if (jarPath == null) {
            jarPath = findJarPath(instance);
        }
        Set<Class<?>> classes = new HashSet<>();
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && name.startsWith("cz/jesuschrist69") &&
                        !name.contains("cz/jesuschrist69/buildsystem/shade")) {
                    try {
                        Class<?> c = Class.forName(name.replace("/", ".").substring(0, name.length() - 6));
                        if (interfaceClass.isAssignableFrom(c) && !c.isInterface()) {
                            classes.add(c);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new BuildSystemException("Failed to load class: {0}", name, e);
                    }
                }
            }
        } catch (Exception e) {
            throw new BuildSystemException("Failed to load classes from jar.", e);
        }
        return classes;
    }

    /**
     * Find the path to the jar file that contains the given class.
     *
     * @param instance The instance of the BuildSystem class.
     * @return The path to the jar file.
     */
    public String findJarPath(@NotNull BuildSystem instance) {
        String path = instance.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        Matcher matcher = JAR_PATTERN.matcher(path);
        if (matcher.find()) {
            String p = matcher.group(1);
            jarPath = "." + p.substring(p.indexOf("/plugins/"));
            return jarPath;
        }
        return null;
    }

    /**
     * Gets a list of all the files in the jar that end with ".yml" and aren't the plugin.yml
     *
     * @param instance The BuildSystem instance.
     * @return A list of all the files in the jar.
     */
    public List<String> getFileListFromJar(@NotNull BuildSystem instance) {
        List<String> files = new ArrayList<>();
        if (jarPath == null) {
            jarPath = findJarPath(instance);
        }
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(".yml") && !entryName.equalsIgnoreCase("plugin.yml")) {
                    files.add(entry.getName());
                }
            }
        } catch (Exception e) {
            throw new BuildSystemException("Failed to get file list from jar.", e);
        }

        return files;
    }


    /**
     * If the file is a directory, call deleteFolder on it, otherwise delete the file.
     *
     * @param file The folder to delete.
     */
    public void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

}
