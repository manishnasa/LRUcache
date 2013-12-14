package lruCache;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lruCache.LRUCacheCommand.CommandType;

/**
 * This is a simple thread safe LRU cache implementation. 
 * @param <K> represents a key type.
 * @param <V> represents a value type.
 */
public class LRUCacheImpl<K,V> implements LRUCache<K,V>
{
    private DataSource<K, V> dataSrc; //represents a data source that gets used in case of cache misses. 

    private Map<K, QueueNode<K, V>> map;
    private LRUQueue<QueueNode<K, V>> queue;
    private Queue<LRUCacheCommand> scheduledOps; 
    private int maxCacheSize;
    private int bulkPurgeSize;

    public LRUCacheImpl(int maxCapacity, DataSource<K,V> dataSource)
    {
        dataSrc = dataSource;  
        maxCacheSize = maxCapacity;
        
        //bulkPurgeSize is calculated as 20% (hard coded) of cache size
        //Instead of purging one element, we purge on batch size of bulkPurgeSize - for efficiency.
        bulkPurgeSize = maxCacheSize/5; 
        
        //using loadFactor 1, overriding the default value of 0.75
        //to avoid rehashing when 3/4th capacity is reached.       
        map = new ConcurrentHashMap<K, QueueNode<K, V>>(maxCacheSize, 1);        
        
        queue = new LRUConcurrentLinkedQueue<QueueNode<K, V>>();
        
        initScheduledOpsExecutor();
    }
    
    private void initScheduledOpsExecutor()
    {
        scheduledOps = new ConcurrentLinkedQueue<LRUCacheCommand>();
        ScheduledOpsExecutor scheduledOpsExecutor = new ScheduledOpsExecutor(scheduledOps);
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
        threadPool.scheduleWithFixedDelay(scheduledOpsExecutor, 0, 100, TimeUnit.NANOSECONDS);       
    }

    /* (non-Javadoc)
     * @see LRUCache#get(java.lang.Object)
     */
    public V get(K key)
    {
        System.out.println("get(key) called with key - " + key);
        V value = null;
        QueueNode<K, V> queueNode = null;
        //We are not synchronizing the below block at the cost of having a possibility where sometimes
        //a recently used element may get purged, and we have an inaccurate LRU cache. This is to increase the concurrency.       
        queueNode = map.get(key);           
        if(queueNode != null)            
        {
            value = queueNode.value;
            queue.moveToFront(queueNode);
        }        
        if( queueNode == null )
        {
            value = cacheMiss(key);
        }
        System.out.println("returning value - " + value);
        return value;          
    }
    
    /**
     * 1. Obtains missing value from the Data Source
     * 2. Updates the cache (map and queue). 
     * @param key the key missing in the cache.
     * @return value obtained from DataSource, and updated in cache.
     */
    private V cacheMiss(K key)
    {
        //1. Get value from Data Source.
        //2. Update the cache.
        //3. Return value.
        System.out.println("Key - " + key + " is missing from the cache.");
        V value = dataSrc.get(key);
        put(key,value);             
        return value;        
    }  
    
    public void put(K key, V value)
    {
        put(key,value,-1);
        return;
    }
        
    public void put(K key, V value, long ttl)
    {
        QueueNode<K,V> queueNode = new QueueNode<K,V>(key, value, ttl);
        LRUCacheCommand putCommand = new LRUCacheCommandImpl<K,V>(this, CommandType.PUT, queueNode);
        scheduledOps.add(putCommand);
    }
      
    public void putNow(K key, V value)
    {
        putNow(key,value,-1);
        return;
    }
    
    public void putNow(K key, V value, long ttl)
    {
        if(map.containsKey(key))
        {
            System.out.println("put(key) returned because key - " + key + " is already present in the cache.");
            return;
        }
        
        QueueNode<K, V> queueNode = null;
        synchronized(this)
        {
            if(map.containsKey(key))
            {
                System.out.println("put(key) returned because key - " + key + " has just been added to the cache.");
                return;
            }
            
            queueNode = new QueueNode<K, V>(key, value);
            map.put(key, queueNode);
            queue.offer(queueNode);
            System.out.println("QueueNode - " + queueNode + " added to the cache.");
            printCache();
        }
        
        if(ttl>0) 
        {
            scheduleTimedRemove(queueNode, ttl);
        }
        purge();
    }
    
    private void scheduleTimedRemove(QueueNode<K, V> queueNode, long ttl)
    {
        LRUCacheCommand removeCommand = new LRUCacheCommandImpl<K,V>(this, CommandType.REMOVE, queueNode);
        //We'll keep the pool size to 1. Because anyways we have a blocking queue. Multiple threads may not add much significance.
        //So TTL removes may not be accurate.
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
        threadPool.schedule(removeCommand, ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * If the LRU queue is over capacity, extra values are deleted to bring the cache to capacity. 
     */
    private void purge()
    {
        if(queue.size() < maxCacheSize)
        {
            return;
        }
        
        synchronized(this)
        {
            //1. We'll check queue size one more time, because it might have changed.
            //2. Instead of purging one element, we will purge in bulk. So we can avoid entering the synchronized block 
            //   several times.
            int size = queue.size();
            while(size>(maxCacheSize-bulkPurgeSize))
            {            
                QueueNode<K,V> queueNode = queue.remove();
                map.remove(queueNode.key);  
                size--;
                System.out.println("QueueNode - " + queueNode + " purged from the cache.");                                       
            }
            printCache();
        }
    }
    
    public void remove(K key)
    {
        QueueNode<K,V> queueNode = new QueueNode<K,V>(key);
        LRUCacheCommand removeCommand = new LRUCacheCommandImpl<K,V>(this, CommandType.REMOVE, queueNode);
        scheduledOps.add(removeCommand);
    }

    /* (non-Javadoc)
     * @see LRUCache#remove(java.lang.Object)
     */
    public void removeNow(K key)
    {
        if(!map.containsKey(key))
        {
            System.out.println("Can't remove key - " + key + ", it's not present in the cache.");
            return;
        }
        
        synchronized(this)
        {
            if(!map.containsKey(key))
            {
                System.out.println("Can't remove key - " + key + ", it's not present in the cache.");
                return;
            }
            
            QueueNode<K, V> queueNode = map.remove(key);
            queue.remove(queueNode);
            System.out.println("QueueNode - " + queueNode + " removed from the cache on request.");       
            printCache();
        }
        
    }
    
    /**
     * This method prints the present state of the cache.
     */
    public void printCache()
    {
        Iterator<QueueNode<K,V>> iter = queue.iterator();
        //Synchronized just for testing purposes.
        //So no one interferes while are printing the cache.
        System.out.print("Cache: ");
        while(iter.hasNext())
        {            
            System.out.print(iter.next() + ", ");
        }
        System.out.println();
    }
}
