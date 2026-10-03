// Problem 2: Warehouse Bin Grid Scan
// Time Complexity: O(m * n)
// Extra Space: O(1)

public class Main {

    static void warehouseSummary(int[][] grid) {
        int total = 0;
        int max = Integer.MIN_VALUE;
        int maxRow = -1;
        int maxCol = -1;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                total += grid[i][j];

                // Use > instead of >= so the first maximum is retained.
                if (grid[i][j] > max) {
                    max = grid[i][j];
                    maxRow = i;
                    maxCol = j;
                }
            }
        }

        System.out.println("Total = " + total);
        System.out.println("Max Coordinate = (" + maxRow + ", " + maxCol + ")");
    }

    public static void main(String[] args) {
        int[][] grid = {
            {4, 9, 2},
            {7, 1, 6},
            {3, 12, 5}
        };

        warehouseSummary(grid);
    }
}
