package cz.jesuschrist69.buildsystem.gui.menus;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.component.WorldType;
import cz.jesuschrist69.buildsystem.data.WorldData;
import cz.jesuschrist69.buildsystem.exceptions.BuildSystemException;
import cz.jesuschrist69.buildsystem.gui.Gui;
import cz.jesuschrist69.buildsystem.gui.GuiItem;
import cz.jesuschrist69.buildsystem.manager.RoleManager;
import cz.jesuschrist69.buildsystem.manager.WorldManager;
import cz.jesuschrist69.buildsystem.mysql.MySQL;
import cz.jesuschrist69.buildsystem.mysql.builder.SqlBuilder;
import cz.jesuschrist69.buildsystem.utils.ColorUtils;
import cz.jesuschrist69.buildsystem.utils.FileUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public final class MainMenu {

    private MainMenu() {
        throw new BuildSystemException("Tried to instantiate utility class.");
    }

    /**
     * This method creates a GUI with a bunch of items in it
     *
     * @param player The player who is opening the GUI
     * @param plugin The plugin instance
     */
    public static void open(@NotNull Player player, @NotNull BuildSystem plugin) {
        Map<Integer, GuiItem> items = new HashMap<>();
        Optional<YamlConfiguration> langFile = plugin.getFileCache().get("lang.yml");
        if (!langFile.isPresent()) {
            throw new BuildSystemException("Could not open main menu for player {0} because file lang.yml is missing!", player.getName());
        }
        YamlConfiguration lang = langFile.get();

        for (int i = 0; i < 36; i++) {
            items.put(i, GuiItem.create().withItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7))
                    .withName("&c")
                    .withClickEvent(event -> event.setCancelled(true))
                    .build());
        }

        RoleManager roleManager = plugin.getRoleManager();

        items.put(11, GuiItem.create()
                .withItem(new ItemStack(Material.CHEST))
                .withName(lang.getString("MENUS.MAIN.ALL-WORLDS.NAME", "&eWorld List"))
                .withLore(lang.getStringList("MENUS.MAIN.ALL-WORLDS.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    openWorldList(player, plugin, 0, null);
                })
                .build());

        if (roleManager.hasPermission(player, RoleManager.Permission.SEARCH_WORLD)) {
            items.put(13, GuiItem.create()
                    .withItem(new ItemStack(Material.COMPASS))
                    .withName(lang.getString("MENUS.MAIN.SEARCH-ITEM.NAME", "&eWorld Search"))
                    .withLore(lang.getStringList("MENUS.MAIN.SEARCH-ITEM.LORE"))
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                        new AnvilGUI.Builder()
                                .plugin(plugin)
                                .text("Search for world...")
                                .onComplete((p, text) -> {
                                    openWorldList(p, plugin, 0, text);
                                    return AnvilGUI.Response.close();
                                })
                                .open(player);
                    })
                    .build());
        }

        items.put(15, GuiItem.create()
                .withItem(new ItemStack(Material.EMERALD))
                .withName(lang.getString("MENUS.MAIN.CREATE-ITEM.NAME", "&aCreate New World"))
                .withLore(lang.getStringList("MENUS.MAIN.CREATE-ITEM.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    createWorldMenu(player, plugin, "", WorldType.EMPTY);
                })
                .build());

        Gui gui = Gui.create()
                .withItems(items)
                .withTitle(lang.getString("MENUS.MAIN.TITLE", "&2&lBuild&f&lSystem"))
                .withSize(3 * 9)
                .withDisabledClicking()
                .build();

        gui.open(player);
    }

    /**
     * This method creates a GUI that allows the player to create a new world
     *
     * @param player The player who opened the GUI
     * @param plugin The plugin instance
     * @param name The name of the world
     * @param generator The type of world to create.
     */
    private static void createWorldMenu(@NotNull Player player, @NotNull BuildSystem plugin, @NotNull String name, @NotNull WorldType generator) {
        Map<Integer, GuiItem> items = new HashMap<>();
        Optional<YamlConfiguration> langFile = plugin.getFileCache().get("lang.yml");
        if (!langFile.isPresent()) {
            throw new BuildSystemException("Could not open create world menu for player {0} because file lang.yml is missing!", player.getName());
        }
        YamlConfiguration lang = langFile.get();
        SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        try {
            if (slimePlugin.getLoader("mysql").worldExists(name)) {
                createWorldMenu(player, plugin, "", generator);
            }
        } catch (Exception ignored) {
        }

        items.put(10, GuiItem.create()
                .withItem(new ItemStack(generator == WorldType.FLAT ? Material.GRASS : Material.BEDROCK))
                .withName(lang.getString("MENUS.CREATE." + (generator == WorldType.FLAT ? "FLAT" : "EMPTY") + "-GENERATOR.NAME"))
                .withLore(lang.getStringList("MENUS.CREATE." + (generator == WorldType.FLAT ? "FLAT" : "EMPTY") + "-GENERATOR.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    createWorldMenu(player, plugin, name, generator == WorldType.EMPTY ? WorldType.FLAT : WorldType.EMPTY);
                })
                .build());

        items.put(13, GuiItem.create()
                .withItem(new ItemStack(Material.NAME_TAG))
                .withName(lang.getString("MENUS.CREATE.WORLD-NAME.NAME", "&eWorld Name: &6%world-name%")
                        .replace("%world-name%", name.equals("") ? "&cNOT SET" : name))
                .withLore(lang.getStringList("MENUS.CREATE.WORLD-NAME.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .text("Set world name")
                            .onComplete((p, text) -> {
                                if (text.equalsIgnoreCase("plugins") || WorldData.exists(text)) {
                                    return AnvilGUI.Response.text("This world name exists!");
                                }
                                createWorldMenu(p, plugin, text, generator);
                                return AnvilGUI.Response.close();
                            })
                            .open(player);
                })
                .build());

        items.put(16, GuiItem.create()
                .withItem(new ItemStack(name.equals("") ? Material.BARRIER : Material.EMERALD))
                .withName(lang.getString("MENUS.CREATE.CREATE-BTN" + (name.equals("") ? "-DISABLED" : "") + ".NAME"))
                .withLore(lang.getStringList("MENUS.CREATE.CREATE-BTN" + (name.equals("") ? "-DISABLED" : "") + ".LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    if (!name.equals("")) {
                        SlimeLoader loader = slimePlugin.getLoader("mysql");
                        SlimePropertyMap propertyMap = new SlimePropertyMap();
                        propertyMap.setString(SlimeProperties.ENVIRONMENT, "normal");
                        propertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
                        propertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
                        propertyMap.setBoolean(SlimeProperties.PVP, false);
                        propertyMap.setString(SlimeProperties.WORLD_TYPE, "flat");
                        MySQL mySQL = plugin.getMySQL();
                        if (generator == WorldType.EMPTY) {
                            try {
                                player.closeInventory();
                                SlimeWorld world = slimePlugin.createEmptyWorld(loader, name, false, propertyMap);
                                slimePlugin.generateWorld(world);
                                SlimePropertyMap map = world.getPropertyMap();
                                Location spawn = new Location(Bukkit.getWorld(name), map.getInt(SlimeProperties.SPAWN_X),
                                        map.getInt(SlimeProperties.SPAWN_Y),
                                        map.getInt(SlimeProperties.SPAWN_Z));

                                player.teleport(spawn);
                                mySQL.execute(new SqlBuilder.Insert("%mysql-table-prefix%" + "world_data")
                                        .columns("name", "locked", "hidden", "owner", "created_at", "world_type")
                                        .values(name, "0", "0", player.getName(), Timestamp.valueOf(LocalDateTime.now()).toString(), "EMPTY")
                                        .build());
                                new WorldData(player.getName(), name, WorldType.EMPTY, Timestamp.valueOf(LocalDateTime.now()), false, false, true);
                            } catch (WorldAlreadyExistsException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            World w = new WorldCreator(name).type(org.bukkit.WorldType.FLAT)
                                    .generateStructures(false)
                                    .createWorld();
                            Bukkit.unloadWorld(w, true);
                            Bukkit.getWorlds().remove(w);
                            try {
                                slimePlugin.importWorld(w.getWorldFolder(), name, loader);
                                SlimeWorld world = slimePlugin.loadWorld(loader, name, false, propertyMap);
                                slimePlugin.generateWorld(world);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            } finally {
                                player.teleport(w.getSpawnLocation());
                                mySQL.execute(new SqlBuilder.Insert("%mysql-table-prefix%" + "world_data")
                                        .columns("name", "locked", "hidden", "owner", "created_at", "world_type")
                                        .values(name, "0", "0", player.getName(), Timestamp.valueOf(LocalDateTime.now()).toString(), "FLAT")
                                        .build());
                                new WorldData(player.getName(), name, WorldType.FLAT, Timestamp.valueOf(LocalDateTime.now()), false, false, true);
                                FileUtils.deleteDir(new File("./" + name));
                            }
                        }
                    }
                })
                .build());

        items.put(31, GuiItem.create()
                .withItem(new ItemStack(Material.REDSTONE))
                .withName(lang.getString("MENUS.CREATE.BACK-BTN.NAME", "&cGo Back"))
                .withLore(lang.getStringList("MENUS.CREATE.BACK-BTN.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    open(player, plugin);
                })
                .build());

        Gui gui = Gui.create()
                .withItems(items)
                .withTitle(lang.getString("MENUS.CREATE.TITLE", "&aCreate new world..."))
                .withSize(4 * 9)
                .withDisabledClicking()
                .build();

        gui.open(player);
    }

    /**
     * This method creates a GUI with a list of worlds, and when you click on a world, it teleports you to it
     *
     * @param player The player who is opening the GUI
     * @param plugin The plugin instance
     * @param scrollPos The page number.
     * @param search The search query
     */
    private static void openWorldList(@NotNull Player player, @NotNull BuildSystem plugin, int scrollPos, String search) {
        RoleManager roleManager = plugin.getRoleManager();
        Map<Integer, GuiItem> items = new HashMap<>();
        Optional<YamlConfiguration> langFile = plugin.getFileCache().get("lang.yml");
        if (!langFile.isPresent()) {
            throw new BuildSystemException("Could not open world list menu for player {0} because file lang.yml is missing!", player.getName());
        }
        YamlConfiguration lang = langFile.get();
        List<WorldData> worlds = WorldManager.getApplicableWorlds(plugin, player, search);

        if (scrollPos > 0) {
            items.put(0, GuiItem.create()
                    .withItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4))
                    .withName(lang.getString("MENUS.ALL-WORLD-LIST.PREVIOUS-PAGE-BUTTON.NAME", "&aScroll Up"))
                    .withLore(lang.getStringList("MENUS.ALL-WORLD-LIST.PREVIOUS-PAGE-BUTTON.LORE"))
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                        openWorldList(player, plugin, scrollPos - 1, search);
                    })
                    .build());
        } else {
            items.put(0, GuiItem.create()
                    .withItem(new ItemStack(Material.STAINED_GLASS_PANE))
                    .withName("&c")
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                    })
                    .build());
        }

        for (int i = 1; i <= 4; i++) {
            int slot = i * 9;
            items.put(slot, GuiItem.create()
                    .withItem(new ItemStack(Material.STAINED_GLASS_PANE))
                    .withName("&c")
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                    })
                    .build());
        }

        items.put(18, GuiItem.create()
                .withItem(new ItemStack(Material.BARRIER))
                .withName(lang.getString("MENUS.ALL-WORLD-LIST.BACK-BUTTON.NAME", "&cGo Back"))
                .withLore(lang.getStringList("MENUS.ALL-WORLD-LIST.BACK-BUTTON.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    open(player, plugin);
                })
                .build());

        if (worlds.size() > 48 + (scrollPos * 8)) {
            items.put(45, GuiItem.create()
                    .withItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5))
                    .withName(lang.getString("MENUS.ALL-WORLD-LIST.NEXT-PAGE-BUTTON.NAME", "&eScroll Down"))
                    .withLore(lang.getStringList("MENUS.ALL-WORLD-LIST.NEXT-PAGE-BUTTON.LORE"))
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                        openWorldList(player, plugin, scrollPos + 1, search);
                    })
                    .build());
        } else {
            items.put(45, GuiItem.create()
                    .withItem(new ItemStack(Material.STAINED_GLASS_PANE))
                    .withName("&c")
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                    })
                    .build());
        }

        int slot = 0;
        for (int i = 1; i < 54; i++) {
            if (i % 9 == 0) continue;
            int offset = scrollPos * 8;
            if (offset + slot >= worlds.size()) break;
            WorldData w = worlds.get(offset + slot);
            items.put(i, GuiItem.create()
                    .withItem(new ItemStack(w.getWorldType() == WorldType.EMPTY ? Material.BEDROCK : Material.GRASS))
                    .withName(lang.getString("MENUS.ALL-WORLD-LIST.WORLD.NAME", "&f%world-owner%&7's world")
                            .replace("%world-owner%", w.getOwner())
                            .replace("%world-name%", w.getName())
                            .replace("%world-created%", w.getFormatTime()))
                    .withLore(replacePholders(lang.getStringList("MENUS.ALL-WORLD-LIST.WORLD.LORE"), w))
                    .withClickEvent(event -> {
                        event.setCancelled(true);
                        if (event.getClick() == ClickType.LEFT) {
                            if (w.isLocked() && !roleManager.hasPermission(player, RoleManager.Permission.LOCK_WORLDS)) {
                                for (String s : lang.getStringList("MESSAGES.NO-TELEPORT-LOCK")) {
                                    player.sendMessage(ColorUtils.colorize(s));
                                }
                                return;
                            }
                            w.teleport(player);
                            for (String s : lang.getStringList("MESSAGES.TELEPORTED")) {
                                s = s.replace("%world-name%", w.getName());
                                player.sendMessage(ColorUtils.colorize(s));
                            }
                        } else if (event.getClick() == ClickType.RIGHT) {
                            deleteWorld(plugin, player, w);
                        }
                    })
                    .build());
            slot++;
        }

        Gui gui = Gui.create()
                .withItems(items)
                .withSize(6 * 9)
                .withDisabledClicking()
                .withTitle(lang.getString("MENUS.ALL-WORLD-LIST.TITLE", "&bListing Worlds...")).build();

        gui.open(player);
    }

    /**
     * This method creates a GUI with two buttons, one to cancel the deletion and one to confirm it
     *
     * @param plugin The plugin instance
     * @param player The player who opened the GUI
     * @param data The WorldData object that is being deleted.
     */
    private static void deleteWorld(@NotNull BuildSystem plugin, @NotNull Player player, @NotNull WorldData data) {
        Map<Integer, GuiItem> items = new HashMap<>();
        Optional<YamlConfiguration> langFile = plugin.getFileCache().get("lang.yml");
        if (!langFile.isPresent()) {
            throw new BuildSystemException("Could not open delete world menu for player {0} because file lang.yml is missing!", player.getName());
        }
        YamlConfiguration lang = langFile.get();

        items.put(11, GuiItem.create()
                .withItem(new ItemStack(Material.BARRIER))
                .withName(lang.getString("MENUS.DELETE.CANCEL-BUTTON.NAME", "&cCancel"))
                .withLore(lang.getStringList("MENUS.DELETE.CANCEL-BUTTON.LORE"))
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    open(player, plugin);
                })
                .build());

        List<String> newLore = new ArrayList<>();
        for (String s : lang.getStringList("MENUS.DELETE.CONFIRM-BUTTON.LORE")) {
            s = s.replace("%world-name%", data.getName());
            newLore.add(s);
        }

        items.put(15, GuiItem.create()
                .withItem(new ItemStack(Material.TNT))
                .withName(lang.getString("MENUS.DELETE.CONFIRM-BUTTON.NAME", "&aConfirm"))
                .withLore(newLore)
                .withClickEvent(event -> {
                    event.setCancelled(true);
                    data.delete(plugin);
                    open(player, plugin);
                    for (String s : lang.getStringList("MESSAGES.WORLD-DELETE")) {
                        s = s.replace("%world-name%", data.getName());
                        player.sendMessage(ColorUtils.colorize(s));
                    }
                })
                .build());

        Gui gui = Gui.create()
                .withTitle(lang.getString("MENUS.DELETE.TITLE", "&cDeleting world..."))
                .withSize(3 * 9)
                .withItems(items)
                .withDisabledClicking()
                .build();

        gui.open(player);
    }

    /**
     * This function replaces the placeholders in the lore with the actual data
     *
     * @param lore The lore of the item.
     * @param data The WorldData object that contains all the information about the world.
     * @return A list of strings.
     */
    private static List<String> replacePholders(@NotNull List<String> lore, @NotNull WorldData data) {
        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            s = s.replace("%world-name%", data.getName()).replace("%world-owner%", data.getOwner())
                    .replace("%world-created%", data.getFormatTime());
            newLore.add(s);
        }
        return newLore;
    }

}
