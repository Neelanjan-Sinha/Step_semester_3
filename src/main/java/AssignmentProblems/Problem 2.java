import java.util.*;

public class Problem2 {
    public static int[] longestStreak(int[] costs, long budget) {
        int bestLen = 0, bestStart = -1;
        int left = 0;
        long sum = 0;

        for (int right = 0; right < costs.length; right++) {
            sum += costs[right];
            while (sum > budget && left <= right) {
                sum -= costs[left];
                left++;
            }
            int len = right - left + 1;   // 0 if the window became empty
            if (len > bestLen) {
                bestLen = len;
                bestStart = left;
            }
        }
        return new int[]{bestLen, bestStart};
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(
            longestStreak(new int[]{4, 2, 1, 7, 3, 1, 2, 1, 5}, 8)));  // [4, 4]
        System.out.println(Arrays.toString(
            longestStreak(new int[]{9, 10}, 8)));                      // [0, -1]
    }
}
