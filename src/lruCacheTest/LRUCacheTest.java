package lruCacheTest;

import apps.MySimpleApp;
import apps.MyThreadedApp;
import apps.MyThreadedApp.TestThreadMode;

public class LRUCacheTest
{
    public static void main(String[] args) throws InterruptedException
    {            
        System.out.println("Running two threads, each trying to first get and then remove a set of 5 elements to the cache. Both sets are identical.");
        MyThreadedApp threadApp2 = new MyThreadedApp(TestThreadMode.REMOVE);
        Thread t3 = new Thread(threadApp2, "Thread 3");
        Thread t4 = new Thread(threadApp2, "Thread 4");
        t3.start();
        t4.start();
        try
        {
            t3.join();
            t4.join();
        }
        catch(InterruptedException e)
        {
            System.out.println(e);
        }
        System.out.println();              
    }
}
