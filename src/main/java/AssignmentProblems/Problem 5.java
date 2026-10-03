import java.util.*;

public class Problem5 {

    public static List<Integer> auditRoute(int[][] grid) {
        List<Integer> route = new ArrayList<>();
        if (grid == null || grid.length == 0 || grid[0].length == 0) return route;

        int top = 0, bottom = grid.length - 1;
        int left = 0, right = grid[0].length - 1;

        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) route.add(grid[top][c]);      // top row
            top++;

            for (int r = top; r <= bottom; r++) route.add(grid[r][right]);    // right column
            right--;

            if (top <= bottom) {
                for (int c = right; c >= left; c--) route.add(grid[bottom][c]); // bottom row
                bottom--;
            }

            if (left <= right) {
                for (int r = bottom; r >= top; r--) route.add(grid[r][left]);   // left column
                left++;
            }
        }
        return route;
    }

    public static void main(String[] args) {
        int[][] grid = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        System.out.println(auditRoute(grid)); // [1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]
        System.out.println(auditRoute(new int[][]{{1, 2, 3}}));          // single row
        System.out.println(auditRoute(new int[][]{{1}, {2}, {3}}));      // single column
        System.out.println(auditRoute(new int[][]{{1, 2}, {3, 4}, {5, 6}})); // [1,2,4,6,5,3]
    }
}
