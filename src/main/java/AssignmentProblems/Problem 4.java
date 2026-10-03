public class Problem4 {
    public static int countInBand(int[] scores, int low, int high) {
        int from = firstAtLeast(scores, low);
        int to = firstGreaterThan(scores, high);
        return to - from;
    }

    // first index i with scores[i] >= target (n if none)
    private static int firstAtLeast(int[] a, int target) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] >= target) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    // first index i with scores[i] > target (n if none)
    private static int firstGreaterThan(int[] a, int target) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] > target) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    public static void main(String[] args) {
        int[] scores = {35, 42, 42, 50, 58, 58, 58, 63, 71, 88};
        System.out.println(countInBand(scores, 42, 58));  // 6
        System.out.println(countInBand(scores, 90, 100)); // 0
    }
}
