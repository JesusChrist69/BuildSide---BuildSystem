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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@BuildSystemCommandExecutor
public class UnlockCommand extends Command {

    private BuildSystem plugin;

    public UnlockCommand() {
        super("unlock", "", "/unlock - unlocks current world", new ArrayList<>());
    }

    /**
     * This function is called when the plugin is enabled.
     *
     * @param plugin The plugin instance.
     */
    public void init(@NotNull BuildSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * If the player has permission, unlock the world
     *
     * @param cs The CommandSender, which is the player who executed the command.
     * @param commandLabel The command label that was used to execute the command.
     * @param args The arguments passed to the command.
     * @return A boolean
     */
    @Override
    public boolean execute(CommandSender cs, String commandLabel, String[] args) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            Optional<YamlConfiguration> langFile = plugin.getFileCache().get("lang.yml");

            if (!plugin.getRoleManager().hasPermission(player, RoleManager.Permission.LOCK_WORLDS)) {
                langFile.ifPresent(lang -> {
                    List<String> message = lang.getStringList("MESSAGES.NO-PERM");
                    if (message == null || message.isEmpty()) return;
                    for (String s : message) {
                        player.sendMessage(ColorUtils.colorize(s));
                    }
                });
                return true;
            }

            String world = player.getWorld().getName();
            for (WorldData wd : WorldData.getWORLDS()) {
                if (wd.getName().equalsIgnoreCase(world)) {
                    if (!wd.isLocked()) {
                        langFile.ifPresent(lang -> {
                            List<String> message = lang.getStringList("MESSAGES.ALREADY-UNLOCKED");
                            if (message == null || message.isEmpty()) return;
                            for (String s : message) {
                                player.sendMessage(ColorUtils.colorize(s));
                            }
                        });
                        break;
                    }
                    wd.toggleLock();
                    wd.save(plugin);
                    langFile.ifPresent(lang -> {
                        List<String> message = lang.getStringList("MESSAGES.WORLD-UNLOCKED");
                        if (message == null || message.isEmpty()) return;
                        for (String s : message) {
                            s = s.replace("%world-name%", world);
                            player.sendMessage(ColorUtils.colorize(s));
                        }
                    });
                    break;
                }
            }
        }

        return true;
    }
}
