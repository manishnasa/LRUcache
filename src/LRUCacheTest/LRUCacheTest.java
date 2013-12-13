package LRUCacheTest;

import apps.MySimpleApp;
import apps.MyThreadedApp;

public class LRUCacheTest
{
    public static void main(String[] args)
    {
        //MySimpleApp app = new MySimpleApp();        
        //app.testLRUPrinciple(); 
        
        MyThreadedApp threadApp = new MyThreadedApp();
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
    }
}
