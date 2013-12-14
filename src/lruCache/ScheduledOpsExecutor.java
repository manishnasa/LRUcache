package lruCache;

import java.util.Iterator;
import java.util.Queue;

public class ScheduledOpsExecutor implements Runnable
{
    Queue<LRUCacheCommand> queue;

    public ScheduledOpsExecutor(Queue<LRUCacheCommand> queue)
    {
        this.queue = queue;
    }

    @Override
    public void run()
    {
        //This could be modified to execute max n commands at a time instead of emptying the queue.
        while(!queue.isEmpty())
        {
            LRUCacheCommand cmd = queue.remove();
            System.out.println("ScheduledOps execution.");
            cmd.execute();
        }
    }
}
