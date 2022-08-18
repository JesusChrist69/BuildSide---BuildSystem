package cz.jesuschrist69.buildsystem.cache.type;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.cache.LocalCache;
import cz.jesuschrist69.buildsystem.manager.RoleManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class RoleCache extends LocalCache<String, RoleManager.RoleUser> {

    public void init(@NotNull BuildSystem plugin) {
        YamlConfiguration settingsFile = plugin.getFileCache().get("settings.yml");
        for (String key : settingsFile.getConfigurationSection("ROLES").getKeys(false)) {
            RoleManager.RoleUser userRole = new RoleManager.RoleUser(settingsFile.getConfigurationSection("ROLES." + key));
            register(key, userRole);
        }
    }

}