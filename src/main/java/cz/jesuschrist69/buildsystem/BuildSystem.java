package cz.jesuschrist69.buildsystem;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import cz.jesuschrist69.buildsystem.cache.type.FileCache;
import cz.jesuschrist69.buildsystem.component.BuildSystemCommandExecutor;
import cz.jesuschrist69.buildsystem.component.BuildSystemListener;
import cz.jesuschrist69.buildsystem.component.WorldType;
import cz.jesuschrist69.buildsystem.data.WorldData;
import cz.jesuschrist69.buildsystem.listeners.WorldListener;
import cz.jesuschrist69.buildsystem.manager.RoleManager;
import cz.jesuschrist69.buildsystem.manager.WorldManager;
import cz.jesuschrist69.buildsystem.mysql.MySQL;
import cz.jesuschrist69.buildsystem.mysql.MysqlCredentials;
import cz.jesuschrist69.buildsystem.mysql.builder.SqlBuilder;
import cz.jesuschrist69.buildsystem.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public final class BuildSystem extends JavaPlugin {

    private MySQL mySQL;
    private FileCache fileCache;
    private RoleManager roleManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        fileCache = new FileCache();
        fileCache.init(this);

        Logger logger = getLogger();

        YamlConfiguration creds = fileCache.get("credentials.yml");
        mySQL = new MySQL(new MysqlCredentials(
                creds.getString("DATABASE.REQUIRED.HOST"),
                creds.getInt("DATABASE.REQUIRED.PORT"),
                creds.getString("DATABASE.REQUIRED.USERNAME"),
                creds.getString("DATABASE.REQUIRED.PASSWORD"),
                creds.getString("DATABASE.REQUIRED.DATABASE"),
                creds.getString("DATABASE.OPTIONAL.TABLE-PREFIX"),
                creds.getBoolean("DATABASE.OPTIONAL.AUTO-RECONNECT")
        ));

        PluginManager pm = Bukkit.getPluginManager();

        if (!mySQL.isConnected()) {
            logger.warning("Failed to connect to mysql database, disabling plugin");
            pm.disablePlugin(this);
            return;
        }

        SlimePlugin slimePlugin = (SlimePlugin) pm.getPlugin("SlimeWorldManager");
        assert slimePlugin != null : "SlimeWorldManager is missing";
        SlimeLoader loader = slimePlugin.getLoader("mysql");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try (ResultSet results = mySQL.getResult(new SqlBuilder.Select("%mysql-table-prefix%" + "world_data")
                    .columns("*").build())) {
                List<String> worlds = loader.listWorlds();
                while (results.next()) {
                    String name = results.getString("name");
                    boolean hidden = results.getInt("hidden") == 1;
                    boolean locked = results.getInt("locked") == 1;
                    String owner = results.getString("owner");
                    String worldType = results.getString("world_type");
                    Timestamp createdAt = results.getTimestamp("created_at");
                    WorldData data = new WorldData(owner, name, WorldType.valueOf(worldType), createdAt, hidden, locked, Bukkit.getWorld(name) != null);
                }
                for (WorldData wd : WorldData.getWORLDS()) {
                    if (!worlds.contains(wd.getName())) {
                        mySQL.execute(new SqlBuilder.Delete("%mysql-table-prefix%" + "world_data")
                                .where("name = '" + wd.getName()+"'")
                                .build());
                    }
                }
            } catch (Exception e) {
                logger.severe("Failed to load worlds!");
                e.printStackTrace();
            }
        });

        // Register all listeners
        try {
            Set<Class<?>> listeners = FileUtils.getClassesForInt(this, BuildSystemListener.class);
            for (Class<?> clazz : listeners) {
                pm.registerEvents((Listener) clazz.newInstance(), this);
            }
        } catch (Exception e) {
            getLogger().warning("Failed to register listeners, disabling plugin");
            pm.disablePlugin(this);
            return;
        }

        // Register all commands
        try {
            Set<Class<?>> commands = FileUtils.getClassesForInt(this, BuildSystemCommandExecutor.class);
            for (Class<?> clazz : commands) {
                Object o = clazz.newInstance();
                Method m = o.getClass().getDeclaredMethod("init", BuildSystem.class);
                m.invoke(o, this);
            }
        } catch (Exception e) {
            getLogger().warning("Failed to register commands, disabling plugin");
            pm.disablePlugin(this);
            return;
        }

        roleManager = new RoleManager(this);

        WorldManager.startChecker(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mySQL.disconnect();
    }

    public FileCache getFileCache() {
        return fileCache;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

}
