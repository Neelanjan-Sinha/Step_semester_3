import java.util.Arrays;

class LoanReceipt {

    // All fields are final
    private final String memberId;
    private final String[] bookIds;

    // Constructor
    public LoanReceipt(String memberId, String[] bookIds) {

        if (bookIds == null) {
            throw new IllegalArgumentException("Invalid book IDs");
        }

        // Validate every book ID
        for (String id : bookIds) {
            if (id == null || !id.matches("BK-\\d{3}")) {
                throw new IllegalArgumentException("Invalid book ID");
            }
        }

        this.memberId = memberId;

        // Defensive copy
        this.bookIds = Arrays.copyOf(bookIds, bookIds.length);
    }

    // Defensive copy on the way out
    public String[] getBookIds() {
        return Arrays.copyOf(bookIds, bookIds.length);
    }

    // With-style method
    public LoanReceipt withCorrectedBookId(int index, String newId) {

        if (index < 0 || index >= bookIds.length) {
            throw new IllegalArgumentException("Invalid index");
        }

        if (newId == null || !newId.matches("BK-\\d{3}")) {
            throw new IllegalArgumentException("Invalid book ID");
        }

        // Make a new array
        String[] newBooks = Arrays.copyOf(bookIds, bookIds.length);

        // Change only the new array
        newBooks[index] = newId;

        // Return a completely new object
        return new LoanReceipt(memberId, newBooks);
    }

    public String getMemberId() {
        return memberId;
    }
}


/*
 * Reference-only receipt.
 *
 * It extends LoanReceipt so that it can be stored in
 * a LoanReceipt[] and identified using instanceof.
 */
class ReferenceOnlyLoanReceipt extends LoanReceipt {

    private final String roomNumber;

    public ReferenceOnlyLoanReceipt(
            String memberId,
            String[] bookIds,
            String roomNumber) {

        super(memberId, bookIds);
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
}


public class Main {

    /*
     * Static shared state.
     * Set only once using a static block.
     */
    static {
        System.out.println("Library circulation system ready");
    }


    /*
     * Processes the nightly circulation.
     *
     * Output format:
     * x processed | y null skipped | z reference-only | w regular
     */
    public static String processNightlyCirculation(LoanReceipt[] receipts) {

        int processed = 0;
        int nullSkipped = 0;
        int referenceOnly = 0;
        int regular = 0;

        if (receipts == null) {
            return "0 processed | 0 null skipped | 0 reference-only | 0 regular";
        }

        for (LoanReceipt receipt : receipts) {

            // Never throw on null entry
            if (receipt == null) {
                nullSkipped++;
                continue;
            }

            processed++;

            if (receipt instanceof ReferenceOnlyLoanReceipt) {
                referenceOnly++;
            } else {
                regular++;
            }
        }

        return processed + " processed | "
                + nullSkipped + " null skipped | "
                + referenceOnly + " reference-only | "
                + regular + " regular";
    }


    public static void main(String[] args) {

        // Example 1
        try {

            LoanReceipt r1 = new LoanReceipt(
                    "LIB-8841",
                    new String[]{"BK-100", "bad"}
            );

            System.out.println("Construction successful");

        } catch (IllegalArgumentException e) {

            System.out.println("construction rejected");
        }


        // Example 2
        LoanReceipt r = new LoanReceipt(
                "LIB-8841",
                new String[]{"BK-100", "BK-101"}
        );

        String[] ids = r.getBookIds();

        // Change returned array
        ids[0] = "HACKED";

        // Original object remains unchanged
        System.out.println(r.getBookIds()[0]);


        // Example 3
        LoanReceipt[] receipts = {
                new ReferenceOnlyLoanReceipt(
                        "LIB-001",
                        new String[]{"BK-200"},
                        "Reading Room 3"
                ),

                null,

                new LoanReceipt(
                        "LIB-002",
                        new String[]{"BK-201"}
                )
        };

        System.out.println(
                processNightlyCirculation(receipts)
        );
    }
}
