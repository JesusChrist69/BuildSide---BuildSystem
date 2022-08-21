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

    /**
     * This function checks every minute if there is a world that is unused for longer than 5 minutes.
     * If there is world that is unused for longer than 5 minutes then we unload that world.
     *
     * @param plugin The plugin instance
     */
    public static void startChecker(@NotNull BuildSystem plugin) {
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

    /**
     * This method returns a list of worlds that the player can see
     *
     * @param plugin The plugin instance.
     * @param player The player who is viewing the worlds.
     * @param search The search string that the player entered.
     * @return A list of WorldData objects.
     */
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
