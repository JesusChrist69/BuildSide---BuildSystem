package cz.jesuschrist69.buildsystem;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import cz.jesuschrist69.buildsystem.cache.type.FileCache;
import cz.jesuschrist69.buildsystem.component.BuildSystemCommandExecutor;
import cz.jesuschrist69.buildsystem.component.BuildSystemListener;
import cz.jesuschrist69.buildsystem.component.WorldType;
import cz.jesuschrist69.buildsystem.data.WorldData;
import cz.jesuschrist69.buildsystem.manager.RoleManager;
import cz.jesuschrist69.buildsystem.manager.WorldManager;
import cz.jesuschrist69.buildsystem.mysql.MySQL;
import cz.jesuschrist69.buildsystem.mysql.MysqlCredentials;
import cz.jesuschrist69.buildsystem.mysql.builder.SqlBuilder;
import cz.jesuschrist69.buildsystem.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
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
        PluginManager pm = Bukkit.getPluginManager();

        Optional<YamlConfiguration> credsFile = fileCache.get("credentials.yml");
        credsFile.ifPresent(creds -> {
            try {
                mySQL = new MySQL(new MysqlCredentials(
                        creds.getString("DATABASE.REQUIRED.HOST"),
                        creds.getInt("DATABASE.REQUIRED.PORT"),
                        creds.getString("DATABASE.REQUIRED.USERNAME"),
                        creds.getString("DATABASE.REQUIRED.PASSWORD"),
                        creds.getString("DATABASE.REQUIRED.DATABASE"),
                        creds.getString("DATABASE.OPTIONAL.TABLE-PREFIX"),
                        creds.getBoolean("DATABASE.OPTIONAL.AUTO-RECONNECT")
                ));
            } catch (Exception e) {
                getLogger().warning("Failed to connect to MySQL database. " +
                        "Please fill all required fields or check if you filled them with correct values. " +
                        "Plugin will now disable as it requires database connection.");
                pm.disablePlugin(this);
            }
        });

        if (mySQL == null || !mySQL.isConnected()) {
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
                                .where("name = '" + wd.getName() + "'")
                                .build());
                    }
                }
            } catch (Exception e) {
                logger.severe("Failed to load worlds!");
                e.printStackTrace();
            }
        });

        Reflections reflections = new Reflections("cz.jesuschrist69.buildsystem");

        // Register all listeners
        try {
            Set<Class<?>> listeners = reflections.getTypesAnnotatedWith(BuildSystemListener.class);
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
            Set<Class<?>> commands = reflections.getTypesAnnotatedWith(BuildSystemCommandExecutor.class);
            Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            Object commandMapObject = f.get(Bukkit.getPluginManager());
            if (commandMapObject instanceof CommandMap) {
                CommandMap commandMap = (CommandMap) commandMapObject;
                for (Class<?> clazz : commands) {
                    Object o = clazz.newInstance();
                    Method m = o.getClass().getDeclaredMethod("init", BuildSystem.class);
                    m.invoke(o, this);
                    commandMap.register(this.getName(), (Command) o);
                }
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

    /**
     * This method returns the file cache object.
     *
     * @return The fileCache object.
     */
    public FileCache getFileCache() {
        return fileCache;
    }

    /**
     * This method returns the MySQL object
     *
     * @return The MySQL object.
     */
    public MySQL getMySQL() {
        return mySQL;
    }

    /**
     * This method returns the roleManager object.
     *
     * @return The roleManager object.
     */
    public RoleManager getRoleManager() {
        return roleManager;
    }

}
