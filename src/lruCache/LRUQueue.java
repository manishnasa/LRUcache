package lruCache;

import java.util.Queue;

public interface LRUQueue<E> extends Queue<E>
{
    boolean moveToFront(E element);
}
