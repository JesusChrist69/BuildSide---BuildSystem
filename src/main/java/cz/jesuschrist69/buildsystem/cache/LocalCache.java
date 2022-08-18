package cz.jesuschrist69.buildsystem.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@ToString
public class LocalCache<K, V> {

    private final Map<K, V> cache = new HashMap<>();

    /**
     * This function registers a key and value in the cache.
     *
     * @param key The key to register the value with.
     * @param value The value to be cached.
     */
    public void register(K key, V value) {
        cache.put(key, value);
    }

    /**
     * Remove the value associated with the given key from the cache.
     *
     * @param key The key to register the value with.
     * @return The value associated with the key.
     */
    public V unregister(K key) {
        return cache.remove(key);
    }

    /**
     * If the key is in the cache, return the value associated with the key, otherwise return null.
     *
     * @param key The key to use to store the value in the cache.
     * @return The value associated with the key.
     */
    public V get(K key) {
        return cache.get(key);
    }

    /**
     * This function clears the cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return The size of the cache.
     */
    public int size() {
        return cache.size();
    }

    /**
     * Return a copy of the cache's values.
     *
     * @return A list of all the values in the cache.
     */
    public List<V> getAll() {
        return new ArrayList<>(cache.values());
    }

}
