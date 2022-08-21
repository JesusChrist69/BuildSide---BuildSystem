package cz.jesuschrist69.buildsystem.cache.type;

import cz.jesuschrist69.buildsystem.BuildSystem;
import cz.jesuschrist69.buildsystem.cache.Cache;
import cz.jesuschrist69.buildsystem.exceptions.BuildSystemException;
import cz.jesuschrist69.buildsystem.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCache implements Cache<String, YamlConfiguration> {

    // Thread safe
    private final Map<String, YamlConfiguration> cache = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * This function loads all the files from the jar file and registers them
     *
     * @param instance The instance of the BuildSystem class.
     */
    public void init(@NotNull BuildSystem instance) {
        String jarPath = FileUtils.findJarPath(instance);
        if (jarPath != null) {
            List<String> fileNames = FileUtils.getFileListFromJar(instance);
            for (String file : fileNames) {
                register(file, FileUtils.loadFile(instance, file));
            }
        } else {
            throw new BuildSystemException("Failed to find jar file. Files could not be loaded!");
        }
    }

    /**
     * This function registers key-value pair to the cache
     *
     * @param key The key to register the value with.
     * @param value The value to be registered.
     */
    @Override
    public void register(String key, YamlConfiguration value) {
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
    public Optional<YamlConfiguration> get(String key) {
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
    public Optional<YamlConfiguration> remove(String key) {
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
    public Collection<YamlConfiguration> values() {
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
