package cache;
import java.util.HashMap;

public class CacheMap<K, V> {
    static class CacheEntry<K> {
        private final K key;
        private CacheEntry<K> next;

        public CacheEntry(K key) {
            this.key = key;
        }
    }

    private final HashMap<K, V> map;
    private CacheEntry<K> first;
    private CacheEntry<K> last;
    private final int cashSize;

    public CacheMap(int cashSize) {
        map = new HashMap<>(cashSize);
        this.cashSize = cashSize;
    }

    public void put(K key, V value) {
        if (map.size() >= cashSize) {
            map.remove(first.key);
            first = first.next;
        }
        
        CacheEntry<K> newEntry = new CacheEntry<>(key);
        
        if (map.size() == 0) {
            first = last = newEntry;
        } else {
            last.next = newEntry;
            last = newEntry;
        }
        map.put(key, value);
    }

    public V get(K key) {
        if (!contains(key)) return null;
        return map.get(key);
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }
}
