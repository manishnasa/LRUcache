package lruCache;

public class QueueNode<K, V>
{
    public V value;
    public K key;
    long ttl;
    
    QueueNode(K key) //used by the remove command.
    {
        this.key = key;
        this.value = null;
        this.ttl = -1;
    }
    
    QueueNode(K key, V val)
    {
        this.key = key;
        this.value = val;
        this.ttl = -1;
    }
    
    QueueNode(K key, V val, long ttl)
    {
        this.key = key;
        this.value = val;
        this.ttl = ttl;
    }
    
    @Override
    public String toString()
    {              
        return ("<" + key + "," + value + ">");
    }
}
