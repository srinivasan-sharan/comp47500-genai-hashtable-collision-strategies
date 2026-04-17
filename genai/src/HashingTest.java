import java.util.Random;
import java.util.stream.IntStream;

public class HashingTest {
    private static final Random rand = new Random(42);
    private static final int NUM_OPS = 10000;
    private static final String[] keys = IntStream.range(0, NUM_OPS)
        .mapToObj(i -> "key" + (rand.nextInt(10000) + i % 100)) // Induces collisions
        .toArray(String[]::new);

    // Generic helper to capture exact types (avoids wildcard issues)
    private static <K, V> long timeOps(CustomHashTableChaining<K, V> ht, K[] testKeys, int ops) {
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            K key = testKeys[i % testKeys.length];
            
            @SuppressWarnings("unchecked")
			V val = (V) Integer.valueOf(i);
            if (i % 3 == 0) ht.put(key, val);
            else if (i % 3 == 1) ht.get(key);
            else ht.remove(key);
        }
        return System.nanoTime() - start;
    }

    private static void testBasic(CustomHashTableChaining<String, Integer> ht, String name) {
        System.out.println("\n=== " + name + " Basic Test ===");
        ht.put("apple", 1);
        ht.put("banana", 2);
        ht.put("apple", 3); // Update existing
        System.out.println("Get apple: " + ht.get("apple")); // Expected: 3
        System.out.println("Size: " + ht.size()); // Expected: 2
        System.out.println("Remove banana: " + ht.remove("banana")); // Expected: 2
        System.out.println("Size after remove: " + ht.size()); // Expected: 1
        System.out.println("Is empty? " + new CustomHashTableChaining<>().isEmpty()); // Expected: true
    }

    private static void testCollisionsResize(CustomHashTableChaining<String, Integer> ht, String name) {
        System.out.println("\n=== " + name + " Collisions/Resize Test ===");
        // Fill to trigger resize (numBuckets=10, load>0.7 ~7+ inserts)
        for (int i = 0; i < 15; i++) {
            ht.put("col" + i, i);
        }
        System.out.println("Size after 15 inserts: " + ht.size()); // Expected: 15
        System.out.println("Get col0: " + ht.get("col0")); // Expected: 0
        ht.remove("col5");
        System.out.println("Size after remove: " + ht.size()); // Expected: 14
    }

    // Probing basic test (no generics issue here)
    private static void testProbingBasic(CustomHashTableProbing<String, Integer> ht) {
        System.out.println("\n=== Probing Basic Test ===");
        ht.put("apple", 1);
        ht.put("banana", 2);
        ht.put("apple", 3); // Update
        System.out.println("Get apple: " + ht.get("apple")); // Expected: 3
        System.out.println("Size: " + ht.size()); // Expected: 2
        System.out.println("Remove apple: " + ht.remove("apple")); // Expected: 3
    }

    public static void main(String[] args) {
        // Chaining tests
        CustomHashTableChaining<String, Integer> chainBasic = new CustomHashTableChaining<>();
        testBasic(chainBasic, "Chaining");

        CustomHashTableChaining<String, Integer> chainResize = new CustomHashTableChaining<>();
        testCollisionsResize(chainResize, "Chaining");

        // Probing test
        CustomHashTableProbing<String, Integer> probe = new CustomHashTableProbing<>();
        testProbingBasic(probe);

        // Performance benchmark
        System.out.println("\n=== Performance Benchmark (10k mixed ops) ===");
        CustomHashTableChaining<String, Integer> chainPerf = new CustomHashTableChaining<>();
        long chainTime = timeOps(chainPerf, keys, NUM_OPS);
        System.out.printf("Chaining avg time: %.1f ns/op\n", (double) chainTime / NUM_OPS);

        // Probing perf (separate generic method if needed)
        CustomHashTableProbing<String, Integer> probePerf = new CustomHashTableProbing<>();
        long probeTime = System.nanoTime();
        for (int i = 0; i < NUM_OPS; i++) {
            String key = keys[i % keys.length];
            int val = i;
            if (i % 3 == 0) probePerf.put(key, val);
            else if (i % 3 == 1) probePerf.get(key);
            else probePerf.remove(key);
        }
        probeTime = System.nanoTime() - probeTime;
        System.out.printf("Probing avg time: %.1f ns/op\n", (double) probeTime / NUM_OPS);
    }
}