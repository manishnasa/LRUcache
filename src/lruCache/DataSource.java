package lruCache;

/**
 * All data sources must implement this interface, to allow the LRUCache to use them.
 * @param <K>
 * @param <V>
 */
public interface DataSource<K, V>
{
    public V get(K key);
}
