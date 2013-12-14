package lruCacheTest;

import apps.MySimpleApp;
import apps.MyThreadedApp;
import apps.MyThreadedApp.TestThreadMode;

public class LRUCacheTest
{
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("Running a simple single threaded application that tests the LRU principle of the cache.");
        MySimpleApp app = new MySimpleApp();        
        app.testLRUPrinciple();
        System.out.println();
        
        System.out.println("Running two threads, each trying to add a set of 5 elements to the cache. Both sets are identical.");
        MyThreadedApp threadApp = new MyThreadedApp(TestThreadMode.PUT);
        Thread t1 = new Thread(threadApp, "Thread 1");
        Thread t2 = new Thread(threadApp, "Thread 2");
        t1.start();
        t2.start();
        try
        {
            t1.join();
            t2.join();
        }
        catch(InterruptedException e)
        {
            System.out.println(e);
        }
        System.out.println();
        
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
        
        System.out.println("Running two threads, one trying to get 5 elements to the cache, other trying to remove the same set of 5 elements.");
        System.out.println("The second thread starts with a delay.");
        MyThreadedApp threadApp3 = new MyThreadedApp(TestThreadMode.GET);
        Thread t5 = new Thread(threadApp3, "Thread 5");        
        threadApp3.setMode(TestThreadMode.REMOVE);
        Thread t6 = new Thread(threadApp3, "Thread 6");
        t5.start();
        Thread.sleep(100);
        t6.start();
        try
        {
            t5.join();
            t6.join();
        }
        catch(InterruptedException e)
        {
            System.out.println(e);
        }
        System.out.println();        
    }
}
