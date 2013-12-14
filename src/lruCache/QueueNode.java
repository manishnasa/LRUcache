package lruCache;

public class QueueNode<K, V>
{
    public V value;
    public K key;
    
    QueueNode(V val)
    {
        this.value = val;
    }
    
    QueueNode(K key, V val)
    {
        this.key = key;
        this.value = val;
    }
    
    @Override
    public String toString()
    {              
        return ("<" + key + "," + value + ">");
    }
}
