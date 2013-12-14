package lruCache;

/**
 * @param <K>
 * @param <V>
 */
public class LRUCacheCommandImpl<K,V> implements LRUCacheCommand, Runnable
{
    CommandType cmdType;
    QueueNode<K, V> queueNode;
    LRUCache<K,V> cache;

    LRUCacheCommandImpl(LRUCache<K,V> cache, CommandType cmdType, QueueNode<K,V> queueNode)
    {
        this.cache = cache;
        this.cmdType = cmdType;
        this.queueNode = queueNode;        
    }
    
    @Override
    public void execute()
    {        
        switch(cmdType)
        {
            case REMOVE:                
                cache.removeNow(queueNode.key);
                break;
            case PUT:
                cache.putNow(queueNode.key, queueNode.value, queueNode.ttl);
                break;                
        }
    }

    @Override
    public void run()
    {
        execute();        
    }
}
