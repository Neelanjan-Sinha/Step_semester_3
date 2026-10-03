// Problem 5: Maximum Sum Subarray of Fixed Size K
// Uses the Sliding Window technique.
// Time Complexity: O(n)
// Extra Space: O(1)

public class Main {

    static int maxSumSubarray(int[] sales, int k) {
        if (sales == null || sales.length == 0 || k <= 0 || k > sales.length) {
            throw new IllegalArgumentException("Invalid array or value of k");
        }

        // Calculate the sum of the first window.
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += sales[i];
        }

        int maxSum = windowSum;

        // Slide the window one position at a time.
        for (int i = k; i < sales.length; i++) {
            windowSum += sales[i];       // Add new element
            windowSum -= sales[i - k];   // Remove old element

            if (windowSum > maxSum) {
                maxSum = windowSum;
            }
        }

        return maxSum;
    }

    public static void main(String[] args) {
        int[] sales = {2, 1, 5, 1, 3, 2};
        int k = 3;

        System.out.println("Maximum Sum = " + maxSumSubarray(sales, k));
    }
}
