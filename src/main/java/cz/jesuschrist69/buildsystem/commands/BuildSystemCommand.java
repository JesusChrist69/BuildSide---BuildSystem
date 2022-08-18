package cz.jesuschrist69.buildsystem.commands;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.component.BuildSystemCommandExecutor;
import cz.jesuschrist69.buildsystem.gui.menus.MainMenu;
import cz.jesuschrist69.buildsystem.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuildSystemCommand implements BuildSystemCommandExecutor {

    private BuildSystem plugin;

    @Override
    public void init(@NotNull BuildSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("buildsystem").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            if (plugin.getRoleManager().getUserRoles(player).isEmpty()) {
                YamlConfiguration lang = plugin.getFileCache().get("lang.yml");
                List<String> message = lang.getStringList("MESSAGES.NO-PERM");
                if (message.isEmpty()) return true;
                for (String s : message) {
                    player.sendMessage(ColorUtils.colorize(s));
                }
                return true;
            }
            MainMenu.open(player, plugin);
        }

        return true;
    }

}
