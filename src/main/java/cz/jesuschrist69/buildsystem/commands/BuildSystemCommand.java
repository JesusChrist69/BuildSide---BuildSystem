package cz.jesuschrist69.buildsystem.commands;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.component.BuildSystemCommandExecutor;
import cz.jesuschrist69.buildsystem.gui.menus.MainMenu;
import cz.jesuschrist69.buildsystem.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BuildSystemCommandExecutor
public class BuildSystemCommand extends Command {

    private BuildSystem plugin;

    public BuildSystemCommand() {
        super("buildsystem", "", "/buildsystem - opens main menu", Arrays.asList("bs", "bsys", "bsystem"));
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
     * If the sender is a player, open the main menu
     *
     * @param cs The CommandSender, this is the player who executed the command.
     * @param commandLabel The command label that was used to execute the command.
     * @param args The arguments passed to the command.
     * @return A boolean
     */
    @Override
    public boolean execute(CommandSender cs, String commandLabel, String[] args) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            if (plugin.getRoleManager().getUserRoles(player).isEmpty()) {
                plugin.getFileCache().get("lang.yml").ifPresent(lang -> {
                    List<String> message = lang.getStringList("MESSAGES.NO-PERM");
                    if (message == null || message.isEmpty()) return;
                    for (String s : message) {
                        player.sendMessage(ColorUtils.colorize(s));
                    }
                });
                return true;
            }
            MainMenu.open(player, plugin);
        }

        return true;
    }
}
