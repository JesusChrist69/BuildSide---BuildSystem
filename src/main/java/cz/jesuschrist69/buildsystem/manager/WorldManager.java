package cz.jesuschrist69.buildsystem.manager;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.data.WorldData;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@UtilityClass
public class WorldManager {

    private static int taskId;
    public static final Map<World, Long> EMPTY_WORLDS = new HashMap<>();

    public static void startChecker(@NotNull BuildSystem plugin) {
        // checks every minute if there is world that is unused for longer than 5 minutes
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            List<World> worlds = new ArrayList<>(EMPTY_WORLDS.keySet());
            for (World w : worlds) {
                long diff = System.currentTimeMillis() - EMPTY_WORLDS.get(w);
                if (diff >= 3e5) {
                    EMPTY_WORLDS.remove(w);
                    System.out.println("Unloaded world " + w.getName());
                    Bukkit.unloadWorld(w, false);
                }
            }
        }, 0, 20*60).getTaskId();
    }

    public List<WorldData> getApplicableWorlds(@NotNull BuildSystem plugin, @NotNull Player player, String search) {
        List<WorldData> data = new ArrayList<>();
        RoleManager roleManager = plugin.getRoleManager();

        for (WorldData wd : WorldData.getWORLDS()) {
            if (roleManager.hasPermission(player, RoleManager.Permission.SEE_ALL_WORLDS)) {
                if (roleManager.hasPermission(player, RoleManager.Permission.HIDE_WORLDS)) {
                    if (search != null && !search.equalsIgnoreCase("")) {
                        if (wd.getName().contains(search)) {
                            data.add(wd);
                        }
                    } else {
                        data.add(wd);
                    }
                } else {
                    if (!wd.isHidden()) {
                        if (search != null && !search.equalsIgnoreCase("")) {
                            if (wd.getName().contains(search)) {
                                data.add(wd);
                            }
                        } else {
                            data.add(wd);
                        }
                    }
                }
            } else {
                if (player.getName().equalsIgnoreCase(wd.getOwner())) {
                    if (search != null && !search.equalsIgnoreCase("")) {
                        if (wd.getName().contains(search)) {
                            data.add(wd);
                        }
                    } else {
                        data.add(wd);
                    }
                }
            }
        }

        return data;
    }

}
