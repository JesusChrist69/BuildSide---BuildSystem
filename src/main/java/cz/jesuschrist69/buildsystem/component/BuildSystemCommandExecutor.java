package cz.jesuschrist69.buildsystem.component;

import cz.jesuschrist69.buildsystem.BuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface BuildSystemCommandExecutor extends CommandExecutor {

    void init(@NotNull BuildSystem plugin);

    @Override
    boolean onCommand(CommandSender cs, Command cmd, String label, String[] args);

}
