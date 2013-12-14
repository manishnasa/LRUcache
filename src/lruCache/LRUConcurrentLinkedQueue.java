package lruCache;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LRUConcurrentLinkedQueue<E> extends ConcurrentLinkedQueue<E> implements LRUQueue<E>
{
    private static final long serialVersionUID = -7961906423672860862L;
    
    synchronized public boolean moveToFront(E element)
    {        
        if( remove(element) )
        {
            if(offer(element))
            {
                return true;
            }
        }
        
        return false;
    }
    
}
