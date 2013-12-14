package lruCache;

/**
 * This interface represents an operation that could be carried out on the LRUCache(Queue and Map).
 */
public interface LRUCacheCommand
{    
    enum CommandType {PUT, REMOVE};
    void execute();
}
