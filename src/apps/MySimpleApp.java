package apps;

import java.util.HashMap;

import LRUCache.DataSource;
import LRUCache.DataSourceImpl;
import LRUCache.LRUCache; 
import LRUCache.LRUCacheImpl;

public class MySimpleApp
{       
    private DataSource<String, String> dataSrc;
    private LRUCache<String, String> cache;
    
    public MySimpleApp()
    {
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
       
    /**
     * This method tests that Least Recently Used object is purged from the cache.
     */
    public void testLRUPrinciple()
    {
        cache.get("a");
        cache.get("b");
        cache.get("c");
        cache.get("d");
        cache.get("e");
        cache.get("f");
        cache.get("g");
        cache.get("h");
        cache.get("i");
        cache.get("j");
        cache.remove("j");
        cache.remove("i");
        cache.get("j");
        cache.get("h");
        cache.put("a", "A");
        cache.put("b", "B");
        cache.get("a");
    }   
}
