package cz.jesuschrist69.buildsystem.cache.type;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.cache.Cache;
import cz.jesuschrist69.buildsystem.manager.RoleManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RoleCache implements Cache<String, RoleManager.RoleUser> {

    // Thread safe
    private final Map<String, RoleManager.RoleUser> cache = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * This function loads all the roles from the settings file and registers them
     *
     * @param plugin The instance of the BuildSystem class.
     */
    public void init(@NotNull BuildSystem plugin) {
        plugin.getFileCache().get("settings.yml").ifPresent(settingsFile -> {
            for (String key : settingsFile.getConfigurationSection("ROLES").getKeys(false)) {
                RoleManager.RoleUser userRole = new RoleManager.RoleUser(settingsFile.getConfigurationSection("ROLES." + key));
                register(key, userRole);
            }
        });
    }

    /**
     * This function registers key-value pair to the cache
     *
     * @param key The key to register the value with.
     * @param value The value to be registered.
     */
    @Override
    public void register(String key, RoleManager.RoleUser value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            // deadlock prevention
            lock.writeLock().unlock();
        }
    }


    /**
     * This function returns size of the cache
     *
     * @return Integer - size of cache
     */
    @Override
    public int size() {
        return cache.size();
    }

   /**
     * This function checks if the cache is empty.
     *
     * @return Boolean
     */
    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * This function clears the cache
     */
    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * "If the key exists in the cache, return the value, otherwise return an empty Optional."
     *
     * The first thing we do is acquire a read lock. This is a lock that allows multiple threads to read from the cache at
     * the same time, but only one thread to write to the cache at a time
     *
     * @param key The key to the cache.
     * @return Optional<RoleManager.RoleUser>
     */
    @Override
    public Optional<RoleManager.RoleUser> get(String key) {
        lock.readLock().lock();
        try {
            return Optional.of(cache.get(key));
        } finally {
            // deadlock prevention
            lock.readLock().unlock();
        }
    }

    /**
     * "If the key exists, remove it from the cache and return the value."
     *
     * The first thing we do is acquire a write lock. This is because we are going to modify the cache
     *
     * @param key The key to remove from the cache.
     * @return Optional<RoleManager.RoleUser>
     */
    @Override
    public Optional<RoleManager.RoleUser> remove(String key) {
        lock.writeLock().lock();
        try {
            return Optional.of(cache.remove(key));
        } finally {
            // deadlock prevention
            lock.writeLock().unlock();
        }
    }

    /**
     * > Returns a collection view of the values contained in this map
     *
     * @return A collection of RoleUser objects
     */
    @Override
    public Collection<RoleManager.RoleUser> values() {
        return cache.values();
    }

    /**
     * Returns a set of all the keys in the cache.
     *
     * @return A set of all the keys in the cache.
     */
    @Override
    public Set<String> keys() {
        return cache.keySet();
    }
}
