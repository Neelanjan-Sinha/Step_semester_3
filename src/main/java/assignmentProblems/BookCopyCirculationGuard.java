public class Main {

    static class BookInventory {

        private int copiesTotal;
        private int copiesAvailable;

        // Constructor
        BookInventory(int copiesTotal) {

            if (copiesTotal <= 0) {
                throw new IllegalArgumentException("Invalid copiesTotal");
            }

            this.copiesTotal = copiesTotal;
            this.copiesAvailable = copiesTotal;
        }

        // Check out a book
        void checkOut() {

            // Do not allow available copies to become negative
            if (copiesAvailable > 0) {
                copiesAvailable--;
            }
        }

        // Check in a book
        void checkIn() {

            // Do not allow available copies to exceed total copies
            if (copiesAvailable < copiesTotal) {
                copiesAvailable++;
            }
        }

        // Return available copies
        int getCopiesAvailable() {
            return copiesAvailable;
        }
    }


    public static void main(String[] args) {

        // Example 1
        try {
            BookInventory b1 = new BookInventory(0);
            System.out.println("Construction successful");
        } catch (IllegalArgumentException e) {
            System.out.println("construction rejected");
        }


        // Example 2
        BookInventory b = new BookInventory(3);

        b.checkOut();
        b.checkOut();
        b.checkOut();
        b.checkOut();       // rejected, already 0

        System.out.println(b.getCopiesAvailable());


        // Example 3
        b.checkIn();
        b.checkIn();
        b.checkIn();
        b.checkIn();        // rejected, already full

        System.out.println(b.getCopiesAvailable());
    }
}
