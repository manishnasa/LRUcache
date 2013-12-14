package lruCache;
import java.util.HashMap;

/**
 * This is a dummy data source implementation. 
 * @param <K> key.
 * @param <V> value.
 */
public class DataSourceImpl<K, V> implements DataSource<K, V>
{   
    private HashMap<K, V> map;
    public DataSourceImpl(HashMap<K,V> map)
    {
        this.map = new HashMap<K,V>(map);
    }
    
    /* (non-Javadoc)
     * @see DataSource#get(java.lang.Object)
     */
    public V get(K key)
    {
        System.out.println("DataSource called to obtain value for key - " + key);
        return map.get(key);
    }

}
