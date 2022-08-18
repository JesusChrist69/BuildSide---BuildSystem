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

    /**
     * This function is called when the plugin is enabled, and it sets the command executor to this class.
     *
     * @param plugin The BuildSystem plugin instance.
     */
    @Override
    public void init(@NotNull BuildSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("buildsystem").setExecutor(this);
    }

    /**
     * If the player has no roles, send them a message, otherwise open the main menu
     *
     * @param cs The CommandSender, which is the player who executed the command.
     * @param cmd The command that was executed.
     * @param label The command label.
     * @param args The arguments that the player typed in.
     * @return A boolean
     */
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
