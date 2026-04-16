import java.util.ArrayList;
import java.util.Objects;

public class CustomHashTableChaining<K, V> {
	private ArrayList<HashNode<K, V>> bucketArray;
	private int numBuckets = 10;
	private int size = 0;
	
	public CustomHashTableChaining() {
		// TODO Auto-generated constructor stub
		bucketArray = new ArrayList<>();
		for (int i = 0; i < numBuckets; i++)
			bucketArray.add(null);
	}
	
	private int getHashCode(K key) {
		return Objects.hashCode(key);
	}
	
	private int getBucketIndex(K key) {
        int hashCode = getHashCode(key);
        int index = hashCode % numBuckets;
        return index < 0 ? -index : index;
    }
	
	public void put(K key, V value) {
        int index = getBucketIndex(key);
        int hashCode = getHashCode(key);
        HashNode<K, V> head = bucketArray.get(index);

        // Update if exists
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode) {
                head.value = value;
                return;
            }
            head = head.next;
        }

        // Insert new
        size++;
        HashNode<K, V> newNode = new HashNode<>(key, value, hashCode);
        newNode.next = bucketArray.get(index);
        bucketArray.set(index, newNode);

        // Resize if load > 0.7
        if (1.0 * size / numBuckets >= 0.7) 
        	resize();
    }

	public V get(K key) {
        int index = getBucketIndex(key);
        HashNode<K, V> head = bucketArray.get(index);
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == getHashCode(key)) return head.value;
            head = head.next;
        }
        return null;
    }

	public V remove(K key) {
        int index = getBucketIndex(key);
        HashNode<K, V> head = bucketArray.get(index);
        HashNode<K, V> prev = null;
        int hashCode = getHashCode(key);

        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode) {
                size--;
                if (prev != null) prev.next = head.next;
                else bucketArray.set(index, head.next);
                return head.value;
            }
            prev = head;
            head = head.next;
        }
        return null;
    }

	private void resize() {
        ArrayList<HashNode<K, V>> temp = bucketArray;
        numBuckets *= 2;
        bucketArray = new ArrayList<>();
        for (int i = 0; i < numBuckets; i++) bucketArray.add(null);
        size = 0;
        for (HashNode<K, V> node : temp) {
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
    }

	public int size() { return size; }
	
    public boolean isEmpty() { return size == 0; }	
}
