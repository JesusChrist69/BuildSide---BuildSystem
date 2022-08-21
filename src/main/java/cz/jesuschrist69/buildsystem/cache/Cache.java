package cz.jesuschrist69.buildsystem.cache;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface Cache<K, V> {

    /**
     * Register a key and value with the registry.
     *
     * @param key The key to register the value with.
     * @param value The value to be registered.
     */
    void register(K key, V value);

    /**
     * Returns the number of elements in the cache.
     *
     * @return The size of the cache.
     */
    int size();

    /**
     * Returns true if the cache is empty, false otherwise.
     *
     * @return A boolean value.
     */
    boolean isEmpty();

    /**
     * Clears the contents of the cache.
     */
    void clear();

    /**
     * If the key is present, return the value associated with it, otherwise return an empty Optional.
     *
     * @param key The key to search for.
     * @return Optional<V>
     */
    Optional<V> get(K key);

    /**
     * Removes the mapping for a key from map if it is present and returns removed value.
     *
     * @param key The key of the entry to remove.
     * @return Optional<V> value that was removed from the map
     */
    Optional<V> remove(K key);

    /**
     * Returns a Collection view of the values contained in this map
     *
     * @return A collection view of the values contained in this map.
     */
    Collection<V> values();

    /**
     * Returns a set of all the keys in the map.
     *
     * @return A set of all the keys in the map.
     */
    Set<K> keys();
}
