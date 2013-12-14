package apps;

import java.util.HashMap;

import lruCache.DataSource;
import lruCache.DataSourceImpl;
import lruCache.LRUCache;
import lruCache.LRUCacheImpl;

public class MyThreadedApp implements Runnable
{
    private DataSource<String, String> dataSrc;
    private LRUCache<String, String> cache;
    
    public enum TestThreadMode {GET,PUT,REMOVE};
    
    private TestThreadMode mode;
    
    public TestThreadMode getMode()
    {
        return mode;
    }

    public void setMode(TestThreadMode mode)
    {
        this.mode = mode;
    }

    public MyThreadedApp(TestThreadMode mode)
    {
        this.mode = mode;
        populateDataSource();
        initCache(5);
    }
    
    private void populateDataSource()
    {
        HashMap<String, String> map =  new HashMap<String, String>();
        map.put("a", "A");
        map.put("b", "B");
        map.put("c", "C");
        map.put("d", "D");
        map.put("e", "E");
        map.put("f", "F");
        map.put("g", "G");
        map.put("h", "H");
        map.put("i", "I");
        map.put("j", "J");
        map.put("k", "K");
        map.put("l", "L");
        map.put("m", "M");
        map.put("n", "N");
        map.put("o", "O");
        map.put("p", "P");
        map.put("q", "Q");
        map.put("r", "R");
        map.put("s", "S");
        map.put("t", "T");
        map.put("u", "U");
        map.put("v", "V");
        map.put("w", "W");
        map.put("x", "X");
        map.put("y", "Y");
        map.put("z", "Z");
        
        dataSrc = new DataSourceImpl<String,String>(map);        
    }
    
    private void initCache(int cacheSize)
    {
        cache = new LRUCacheImpl<String, String>(cacheSize, dataSrc);
    }
    
    private void put5()
    {
        cache.put("a","A");
        cache.put("b","B");
        cache.put("c","C");
        cache.put("d","D");
        cache.put("e","E");              
    }
    
    private void get5()
    {
        cache.get("a");
        cache.get("b");
        cache.get("c");
        cache.get("d");
        cache.get("e");              
    }
    
    private void remove5()
    {
        get5();
        cache.remove("a");
        cache.remove("b");
        cache.remove("c");
        cache.remove("d");
        cache.remove("e");              
    }
       
    @Override
    public void run()
    {
        switch(mode)
        {
            case GET:
                get5();
                break;
            case PUT:
                put5();
                break;
            case REMOVE:
                remove5();
                break;
        }
    }    
}
