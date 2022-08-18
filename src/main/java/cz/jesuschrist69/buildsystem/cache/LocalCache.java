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

    public void register(K key, V value) {
        cache.put(key, value);
    }

    public V unregister(K key) {
        return cache.remove(key);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    public List<V> getAll() {
        return new ArrayList<>(cache.values());
    }

}
