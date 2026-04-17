public class CustomHashTableProbing<K, V> {
    private Entry<K, V>[] table;
    private int size = 0;
    private int capacity = 11; // Prime for better distribution

    @SuppressWarnings("unchecked")
    public CustomHashTableProbing() {
        table = new Entry[capacity];
    }

    private int getHashCode(K key) { return Math.abs(key.hashCode() % capacity); }

    private int probe(K key, int i) {
        return (getHashCode(key) + i) % capacity;
    }

    public void put(K key, V value) {
        int i = 0;
        while (true) {
            int index = probe(key, i++);
            if (table[index] == null || table[index].deleted) {
                table[index] = new Entry<>(key, value);
                table[index].deleted = false;
                size++;
                if (1.0 * size / capacity >= 0.7) resize();
                return;
            }
            if (table[index].key.equals(key)) {
                table[index].value = value;
                return;
            }
        }
    }

    public V get(K key) {
        int i = 0;
        while (true) {
            int index = probe(key, i++);
            if (table[index] == null) return null;
            if (!table[index].deleted && table[index].key.equals(key)) return table[index].value;
        }
    }

    public V remove(K key) {
        int i = 0;
        while (true) {
            int index = probe(key, i++);
            if (table[index] == null) return null;
            if (!table[index].deleted && table[index].key.equals(key)) {
                table[index].deleted = true;
                size--;
                return table[index].value;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] old = table;
        capacity = nextPrime(capacity * 2);
        table = new Entry[capacity];
        size = 0;
        for (Entry<K, V> e : old) {
            if (e != null && !e.deleted) put(e.key, e.value);
        }
    }

    private int nextPrime(int n) {
        // Simple prime finder (extend as needed)
        if (n % 2 == 0) n++;
        while (!isPrime(n)) n += 2;
        return n;
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }

    public int size() { return size; }
}