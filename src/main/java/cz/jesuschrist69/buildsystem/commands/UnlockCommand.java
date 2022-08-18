package cz.jesuschrist69.buildsystem.commands;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.component.BuildSystemCommandExecutor;
import cz.jesuschrist69.buildsystem.data.WorldData;
import cz.jesuschrist69.buildsystem.manager.RoleManager;
import cz.jesuschrist69.buildsystem.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnlockCommand implements BuildSystemCommandExecutor {

    private BuildSystem plugin;

    /**
     * "This function is called when the plugin is enabled, and it sets the command executor for the command 'unlock' to
     * this class."
     *
     * The @NotNull annotation is a Java annotation that tells the compiler that the BuildSystem parameter will never be
     * null
     *
     * @param plugin The BuildSystem plugin instance.
     */
    @Override
    public void init(@NotNull BuildSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("unlock").setExecutor(this);
    }

    /**
     * If the player has permission to lock worlds, and the world is locked, unlock it
     *
     * @param cs The CommandSender who executed the command.
     * @param cmd The command that was executed.
     * @param label The command label.
     * @param args The arguments that the player typed in.
     * @return A boolean
     */
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            YamlConfiguration lang = plugin.getFileCache().get("lang.yml");
            if (!plugin.getRoleManager().hasPermission(player, RoleManager.Permission.LOCK_WORLDS)) {
                List<String> message = lang.getStringList("MESSAGES.NO-PERM");
                if (message.isEmpty()) return true;
                for (String s : message) {
                    player.sendMessage(ColorUtils.colorize(s));
                }
                return true;
            }

            String world = player.getWorld().getName();
            for (WorldData wd : WorldData.getWORLDS()) {
                if (wd.getName().equalsIgnoreCase(world)) {
                    if (!wd.isLocked()) {
                        for (String s : lang.getStringList("MESSAGES.ALREADY-UNLOCKED")) {
                            player.sendMessage(ColorUtils.colorize(s));
                        }
                        break;
                    }
                    wd.toggleLock();
                    wd.save(plugin);
                    for (String s : lang.getStringList("MESSAGES.WORLD-UNLOCKED")) {
                        s = s.replace("%world-name%", world);
                        player.sendMessage(ColorUtils.colorize(s));
                    }
                    break;
                }
            }
        }

        return true;
    }
}
