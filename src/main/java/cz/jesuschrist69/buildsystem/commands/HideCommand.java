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

public class HideCommand implements BuildSystemCommandExecutor {

    private BuildSystem plugin;

    @Override
    public void init(@NotNull BuildSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("hide").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            YamlConfiguration lang = plugin.getFileCache().get("lang.yml");
            if (!plugin.getRoleManager().hasPermission(player, RoleManager.Permission.HIDE_WORLDS)) {
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
                    if (wd.isHidden()) {
                        for (String s : lang.getStringList("MESSAGES.ALREADY-HIDDEN")) {
                            player.sendMessage(ColorUtils.colorize(s));
                        }
                        break;
                    }
                    wd.toggleHidden();
                    wd.save(plugin);
                    for (String s : lang.getStringList("MESSAGES.WORLD-HIDDEN")) {
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
