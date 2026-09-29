// Save as Main.java
class EventTicket {
    private static int counter = 1000;       // shared across all tickets
    private static int ticketsIssued = 0;

    public final String ticketId;            // final: set once, never reassigned
    private final double basePrice;
    private double amountPaid;

    public EventTicket(double basePrice) {
        if (basePrice <= 0) throw new IllegalArgumentException("basePrice must be positive");
        counter++;                           // increment first, then build the id
        ticketsIssued++;
        this.ticketId = "TCK-" + counter;
        this.basePrice = basePrice;
    }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    void pay(double amount, String mode) {
        System.out.println("Paying " + amount + " via " + mode);
        pay(amount);                         // reuse the flat version
    }

    double getBalanceDue() { return basePrice - amountPaid; }

    static boolean isValidPromoCode(String code) {
        if (code == null || code.length() != 5) return false;   // length first
        return code.charAt(0) == 'F'
                && Character.isDigit(code.charAt(1))
                && Character.isDigit(code.charAt(2))
                && Character.isDigit(code.charAt(3))
                && Character.isUpperCase(code.charAt(4));
    }

    static int getTicketsIssued() { return ticketsIssued; }
}

class GroupTicket extends EventTicket {
    private final int groupSize;

    public GroupTicket(double basePrice, int groupSize) {
        super(basePrice);
        if (groupSize <= 0) throw new IllegalArgumentException("groupSize must be positive");
        this.groupSize = groupSize;
    }

    int getGroupSize() { return groupSize; }
}

public class Main {
    static String processNightlySettlement(EventTicket[] tickets) {
        int processed = 0, nulls = 0, groups = 0, individuals = 0;
        for (EventTicket t : tickets) {
            if (t == null) { nulls++; continue; }
            processed++;
            if (t instanceof GroupTicket) groups++;
            else individuals++;
        }
        return processed + " processed | " + nulls + " null skipped | "
                + groups + " group | " + individuals + " individual";
    }

    public static void main(String[] args) {
        EventTicket t1 = new EventTicket(500);
        System.out.println(t1.ticketId);                       // TCK-1001
        System.out.println(EventTicket.getTicketsIssued());    // 1

        System.out.println(EventTicket.isValidPromoCode("F123A")); // true
        System.out.println(EventTicket.isValidPromoCode("F12A"));  // false
        System.out.println(EventTicket.isValidPromoCode("X123A")); // false

        t1.pay(200);
        t1.pay(200, "UPI");
        System.out.println(t1.getBalanceDue());                // 100.0

        System.out.println(processNightlySettlement(new EventTicket[]{
                new GroupTicket(2000, 5), null, new EventTicket(500)}));
        // 2 processed | 1 null skipped | 1 group | 1 individual
    }
}
