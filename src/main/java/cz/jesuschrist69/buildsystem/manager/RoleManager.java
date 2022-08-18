package cz.jesuschrist69.buildsystem.manager;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.cache.type.RoleCache;
import cz.jesuschrist69.buildsystem.component.Role;
import cz.jesuschrist69.buildsystem.component.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class RoleManager {

    private final RoleCache roleCache;

    public RoleManager(@NotNull BuildSystem plugin) {
        this.roleCache = new RoleCache();

        roleCache.init(plugin);
    }

    public List<RoleUser> getUserRoles(@NotNull Player player) {
        List<RoleUser> roles = new ArrayList<>();
        for (RoleUser role : roleCache.getAll()) {
            if (player.hasPermission(role.getRequiredPermission())) {
                roles.add(role);
            }
        }
        return roles;
    }

    public boolean hasPermission(@NotNull Player player, @NotNull Permission permission) {
        List<RoleUser> roles = getUserRoles(player);
        for (RoleUser role : roles) {
            switch (permission) {
                case WORLD_TYPE_EMPTY:
                    if (role.getAllowedWorldTypes().contains(WorldType.EMPTY)) return true;
                case WORLD_TYPE_FLAT:
                    if (role.getAllowedWorldTypes().contains(WorldType.FLAT)) return true;
                case SEARCH_WORLD:
                    if (role.isSearchAllowed()) return true;
                case TELEPORT_TO_WORLD:
                    if (role.isTeleportAllowed()) return true;
                case SEE_ALL_WORLDS:
                    if (role.canSeeAllWorlds()) return true;
                case DELETE_WORLDS:
                    if (role.canDeleteWorlds()) return true;
                case LOCK_WORLDS:
                    if (role.canLockWorlds()) return true;
                case HIDE_WORLDS:
                    if (role.canHideWorlds()) return true;
            }
        }
        return false;
    }

    public enum Permission {
        WORLD_TYPE_EMPTY,
        WORLD_TYPE_FLAT,
        SEARCH_WORLD,
        TELEPORT_TO_WORLD,
        SEE_ALL_WORLDS,
        DELETE_WORLDS,
        LOCK_WORLDS,
        HIDE_WORLDS
    }

    public static class RoleUser implements Role {

        private final ConfigurationSection section;

        public RoleUser(@NotNull ConfigurationSection section) {
            this.section = section;
        }

        @Override
        public String getRoleKey() {
            return section.getName();
        }

        @Override
        public String getRequiredPermission() {
            return section.getString("PERMISSION", "buildsystem." + section.getName().toLowerCase());
        }

        @Override
        public List<WorldType> getAllowedWorldTypes() {
            List<WorldType> worldTypes = new ArrayList<>();
            for (String s : section.getStringList("ALLOWED-WORLD-TYPES")) {
                try {
                    WorldType worldType = WorldType.valueOf(s.toUpperCase());
                    worldTypes.add(worldType);
                } catch (Exception ignored) {}
            }
            if (worldTypes.isEmpty()) {
                worldTypes.add(WorldType.NONE);
            }
            return worldTypes;
        }

        @Override
        public boolean isSearchAllowed() {
            return section.getBoolean("ALLOW-SEARCH", true);
        }

        @Override
        public boolean isTeleportAllowed() {
            return section.getBoolean("WORLD-TP", true);
        }

        @Override
        public boolean canSeeAllWorlds() {
            return section.getBoolean("SEE-ALL-WORLDS", false);
        }

        @Override
        public boolean canDeleteWorlds() {
            return section.getBoolean("DELETE-WORLDS", false);
        }

        @Override
        public boolean canLockWorlds() {
            return section.getBoolean("WORLD-LOCK", false);
        }

        @Override
        public boolean canHideWorlds() {
            return section.getBoolean("WORLD-HIDE", false);
        }
    }

}
