package cache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Cache<K, V> {
    private final ConcurrentHashMap<K, V> map;
    private final ConcurrentLinkedQueue<K> cashOrder;
    private final int cashSize;

    public Cache(int cashSize) {
        map = new ConcurrentHashMap<>(cashSize);
        cashOrder = new ConcurrentLinkedQueue<>();
        this.cashSize = cashSize;
    }

    public void put(K key, V value) {
        if (!contains(key)) {
            if (cashOrder.size() >= cashSize) {
                map.remove(cashOrder.remove());
            }
            map.put(key, value);
            cashOrder.add(key);
        }
    }

    public V get(K key) {
        if (!contains(key)) return null;
        return map.get(key);
    }

    public boolean contains(K key) {
        return map.contains(key);
    }
}