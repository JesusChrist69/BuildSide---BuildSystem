package cz.jesuschrist69.buildsystem.component;

import cz.jesuschrist69.buildsystem.BuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface BuildSystemCommandExecutor extends CommandExecutor {

    /**
     * Initialize the plugin with the given BuildSystem object.
     *
     * @param plugin The plugin instance.
     */
    void init(@NotNull BuildSystem plugin);

    /**
     * "This function is called when a command is executed."
     *
     * The first parameter is the CommandSender. This is the person who executed the command. It can be a player, the
     * console, or a command block
     *
     * @param cs The CommandSender who sent the command.
     * @param cmd The command that was executed.
     * @param label The command label.
     * @param args The arguments passed to the command.
     * @return A boolean.
     */
    @Override
    boolean onCommand(CommandSender cs, Command cmd, String label, String[] args);

}
