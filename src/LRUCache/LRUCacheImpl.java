package LRUCache;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is a simple LRU cache implementation. 
 * @param <K> represents a key type.
 * @param <V> represents a value type.
 */
public class LRUCacheImpl<K,V> implements LRUCache<K,V>
{
    private DataSource<K, V> dataSrc; //represents a data source that gets used in case of cache misses. 

    private Map<K, QueueNode<K, V>> map;
    private Queue<QueueNode<K, V>> queue;
    private int maxCacheSize;
    private int bulkPurgeSize;

    /**
     * @param maxCapacity
     * @param dataSource
     */
    public LRUCacheImpl(int maxCapacity, DataSource<K,V> dataSource)
    {
        dataSrc = dataSource;  
        maxCacheSize = maxCapacity;
        bulkPurgeSize = maxCacheSize/5;
        //using loadFactor 1, overriding the default value of 0.75
        //to avoid rehashing when 3/4th capacity is reached.       
        map = new ConcurrentHashMap<K, QueueNode<K, V>>(maxCacheSize, 1);        
        queue = new ConcurrentLinkedQueue<QueueNode<K, V>>();        
    }


    /* (non-Javadoc)
     * @see LRUCache#get(java.lang.Object)
     */
    public V get(K key)
    {
        System.out.println("get(key) called with key - " + key);
        V value = null;
        QueueNode<K, V> queueNode = map.get(key);
        if( queueNode == null )
        {
            value = cacheMiss(key);
        }
        else            
        {
            //Typically the get method should be synchronized. 
            //If there are two threads. Thread 1 - trying to get the LRU value from the queue.
            //Thread 2 - trying to add a new value, and purge the LRU value.
            //Thread 1 executes first, get the value from the cache but gets interrupted before updating the queue
            //And thread 2 executes then, purging the LRU value - we have an incorrect LRU cache.
            //We are allowing that to happen here to support greater concurrency.
            value = queueNode.value;            
            moveToQueueFront(queueNode);            
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

    /**
     * Moves a value up to the front of the queue. 
     * This maintains the 'Least Recently Used' policy of the queue.
     * @param queueNode The queueValue to be moved up in the queue.
     */
    private void moveToQueueFront(QueueNode<K, V> queueNode)
    {
        //The sought value is already in cache. 
        //Just move up the element in the queue to mark it as recently used.
        
        if(queue.peek()==queueNode) //already at front.
        {
            return;
        }
        
        synchronized(queue)
        {
            //queue.remove might fail if a contending thread removes the object beforehand, 
            //we will still go ahead and do the queue.offer.
            queue.remove(queueNode);  
            queue.offer(queueNode);
        }
        System.out.println("Moved QueueNode - " + queueNode + " to the front of the queue.");                           
        printCache();
    }

    /* (non-Javadoc)
     * @see LRUCache#put(java.lang.Object, java.lang.Object)
     */
    public void put(K key, V value)
    {
        if(map.containsKey(key))
        {
            System.out.println("put(key) returned because key - " + key + " is already present in the cache.");
            return;
        }
                         
        QueueNode<K, V> queueNode = new QueueNode<K, V>(key, value);
        if(map.put(key, queueNode)==null) //this would not be null, if a contending thread has already added the same key.
        {
            queue.offer(queueNode);
            System.out.println("QueueNode - " + queueNode + " added to the cache.");
            printCache();
        }
        System.out.println("QueueNode - " + queueNode + " already added to the cache by a contending thread.");        
        purge();
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
        
        //The lock is on the queue. So while purging is going on, there could be operations happening on the map.
        synchronized(queue)
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
                printCache();
            }
        }
    }

    /* (non-Javadoc)
     * @see LRUCache#remove(java.lang.Object)
     */
    public void remove(K key)
    {       
        QueueNode<K, V> queueNode = map.remove(key);
        if(queueNode!=null) //queueNode could be null if another contending thread has already removed from map.
        {
            queue.remove(queueNode);
        }
        System.out.println("QueueNode - " + queueNode + " removed from the cache on request.");       
        printCache();
    }
    
    /**
     * This method prints the present state of the cache.
     */
    public void printCache()
    {
        Iterator<QueueNode<K,V>> iter = queue.iterator();
        System.out.print("Cache: ");
        while(iter.hasNext())
        {            
            System.out.print(iter.next() + ", ");
        }
        System.out.println();        
    }
}
