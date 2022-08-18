package cz.jesuschrist69.buildsystem.data;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.component.WorldType;
import cz.jesuschrist69.buildsystem.manager.WorldManager;
import cz.jesuschrist69.buildsystem.mysql.builder.SqlBuilder;
import cz.jesuschrist69.buildsystem.utils.FileUtils;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class WorldData {

    @Getter
    private static final List<WorldData> WORLDS = new ArrayList<>();

    public static boolean exists(@NotNull String name) {
        for (WorldData wd : WORLDS) {
            if (wd.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private final String owner;
    private final String name;
    private final WorldType worldType;
    private final Timestamp createdAt;

    private boolean hidden;
    private boolean locked;
    private boolean loaded;

    public WorldData(String owner, String name, WorldType worldType, Timestamp createdAt) {
        this(owner, name, worldType, createdAt, false, false, false);
    }

    public WorldData(String owner, String name, WorldType worldType, Timestamp createdAt, boolean hidden, boolean locked, boolean loaded) {
        this.owner = owner;
        this.name = name;
        this.worldType = worldType;
        this.createdAt = createdAt;
        this.hidden = hidden;
        this.locked = locked;
        this.loaded = loaded;

        WORLDS.add(this);
    }

    private boolean checkIfLoaded() {
        this.loaded = Bukkit.getWorld(name) != null;
        return loaded;
    }

    public void toggleHidden() {
        this.hidden = !hidden;
    }

    public void toggleLock() {
        this.locked = !locked;
    }

    public String getFormatTime() {
        LocalDateTime d = createdAt.toLocalDateTime();
        return d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear() + " " + df(d.getHour()) + ":" + df(d.getMinute()) + ":" + df(d.getSecond());
    }

    private String df(int a) {
        if (a < 10) return "0" + a;
        return "" + a;
    }

    public void teleport(@NotNull Player player) {
        if (Bukkit.getWorld(name) != null) {
            player.teleport(Bukkit.getWorld(name).getSpawnLocation());
            return;
        }
        // load world
        SlimePropertyMap propertyMap = new SlimePropertyMap();
        propertyMap.setString(SlimeProperties.ENVIRONMENT, "normal");
        propertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        propertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        propertyMap.setBoolean(SlimeProperties.PVP, false);
        propertyMap.setString(SlimeProperties.WORLD_TYPE, "flat");

        SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        SlimeLoader loader = slimePlugin.getLoader("mysql");
        try {
            SlimeWorld a = slimePlugin.loadWorld(loader, name, false, propertyMap);
            slimePlugin.generateWorld(a);
            this.loaded = true;
        } catch (WorldInUseException e) {
            try {
                loader.unlockWorld(name);
                teleport(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            player.teleport(Bukkit.getWorld(name).getSpawnLocation());
        }
    }

    public void delete(@NotNull BuildSystem plugin) {
        World w = Bukkit.getWorld(name);
        World a = Bukkit.getWorlds().get(0);
        int i = 0;
        while (a == w) {
            if (Bukkit.getWorlds().size() <= i) {
                for (Player p : a.getPlayers()) {
                    p.kickPlayer("World you were in was deleted and there were no other worlds where you could be teleported.");
                }
                break;
            }
            a = Bukkit.getWorlds().get(i);
            i++;
        }

        if (w != null) {
            for (Player p : w.getPlayers()) {
                p.teleport(a.getSpawnLocation());
            }
            WorldManager.EMPTY_WORLDS.remove(w);
            Bukkit.unloadWorld(w, false);
            Bukkit.getWorlds().remove(w);
        }

        SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        SlimeLoader loader = slimePlugin.getLoader("mysql");
        try {
            loader.deleteWorld(name);
            plugin.getMySQL().execute(new SqlBuilder.Delete("%mysql-table-prefix%" + "world_data")
                    .where("name = '" + name + "'")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUtils.deleteDir(new File("./" + name));
        WORLDS.remove(this);
    }

    public void save(@NotNull BuildSystem plugin) {
        plugin.getMySQL().execute(new SqlBuilder.Update("%mysql-table-prefix%" + "world_data")
                .columns("hidden", "locked")
                .values(hidden ? "1" : "0", locked ? "1" : "0")
                .where("name = '" + name+"'")
                .build());
    }

}
