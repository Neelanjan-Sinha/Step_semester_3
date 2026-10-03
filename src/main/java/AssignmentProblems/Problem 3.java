import java.util.*;

public class Problem3 {
    public static long countPeriods(int[] transactions, long k) {
        Map<Long, Integer> seen = new HashMap<>();
        seen.put(0L, 1);

        long prefix = 0;
        long count = 0;
        for (int t : transactions) {
            prefix += t;
            count += seen.getOrDefault(prefix - k, 0);
            seen.merge(prefix, 1, Integer::sum);
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(countPeriods(new int[]{3, 4, -7, 1, 3, 3, 1, -4}, 7)); // 4
        System.out.println(countPeriods(new int[]{1, 2, 3}, 10));                 // 0
    }
}
