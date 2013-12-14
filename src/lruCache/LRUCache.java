package lruCache;

/**
 * All implementations of LRUCache must implement this interface,
 * to allow any app to use the cache without worrying about the implementation.
 * @param <K> The datatype for Key.
 * @param <V> The datatype for Value.
 */
public interface LRUCache<K, V>
{
    public V get(K key);
    public void put(K key, V value);
    public void putNow(K key, V value);
    public void put(K key, V value, long ttl);
    public void putNow(K key, V value, long ttl);
    public void remove(K key);
    public void removeNow(K key);
}
