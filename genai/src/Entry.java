
public class Entry<K, V> {
	K key;
	V value;
	boolean deleted = false;
	
	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
	}

}
