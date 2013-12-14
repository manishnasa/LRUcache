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
                System.out.println("Timeout for QueueNode - " + queueNode);
                cache.remove(queueNode.key);
                break;
            case PUT:
                cache.put(queueNode.key, queueNode.value);
                break;                
        }
    }

    @Override
    public void run()
    {
        execute();        
    }
}
