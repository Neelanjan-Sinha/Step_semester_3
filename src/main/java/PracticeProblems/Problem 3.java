// Problem 3: Library Catalog Lookup
// Uses Binary Search because the catalog is sorted by ISBN.
// Time Complexity: O(log n)
// Extra Space: O(1)

public class Main {

    static class Book {
        String isbn;
        String title;

        Book(String isbn, String title) {
            this.isbn = isbn;
            this.title = title;
        }
    }

    static String findBook(Book[] catalog, String targetIsbn) {
        int left = 0;
        int right = catalog.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            int comparison = catalog[mid].isbn.compareTo(targetIsbn);

            if (comparison == 0) {
                return catalog[mid].title;
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return "Not Found";
    }

    public static void main(String[] args) {
        Book[] catalog = {
            new Book("0001112223", "Introduction to Algebra"),
            new Book("0002223334", "Beginning Python"),
            new Book("0003334445", "Classic Mythology"),
            new Book("0004445556", "Data and Society"),
            new Book("0005556667", "European History")
        };

        String targetIsbn = "0003334445";

        System.out.println("Result: " + findBook(catalog, targetIsbn));
    }
}

