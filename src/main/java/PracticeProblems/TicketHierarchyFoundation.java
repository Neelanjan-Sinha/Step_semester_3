// Save as Main.java
class EventTicket {
    private final String attendeeId;
    private final double basePrice;
    private double amountPaid;

    public EventTicket(String attendeeId, double basePrice) {
        // Single place where the validation rule lives
        if (attendeeId == null || attendeeId.strip().length() < 4) {
            throw new IllegalArgumentException("attendeeId must be non-blank and at least 4 characters");
        }
        if (basePrice <= 0) {
            throw new IllegalArgumentException("basePrice must be positive");
        }
        this.attendeeId = attendeeId;
        this.basePrice = basePrice;
    }

    protected String getAttendeeId() { return attendeeId; }
    protected double getBasePrice()  { return basePrice; }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    double getBalanceDue() { return basePrice - amountPaid; }

    static String registerBatch(String[] attendeeIds, double basePrice) {
        int registered = 0, rejected = 0;
        for (String id : attendeeIds) {
            try {
                new EventTicket(id, basePrice);
                registered++;
            } catch (IllegalArgumentException e) {
                rejected++;
            }
        }
        return "Registered: " + registered + " | Rejected: " + rejected;
    }
}

class WorkshopTicket extends EventTicket {
    private final String track;

    public WorkshopTicket(String attendeeId, double basePrice, String track) {
        super(attendeeId, basePrice);   // no duplicate fields
        this.track = track;
    }

    String getTrack() { return track; }
}

public class Main {
    public static void main(String[] args) {
        try {
            new EventTicket("ST1", 500);
        } catch (IllegalArgumentException e) {
            System.out.println("construction rejected");
        }

        WorkshopTicket w = new WorkshopTicket("STU2", 1200, "AI/ML");
        w.pay(500);
        System.out.println(w.getBalanceDue());   // 700.0

        System.out.println(EventTicket.registerBatch(
                new String[]{"STU1", "ST1", "STU2", " ", "STU3"}, 500));
        // Registered: 3 | Rejected: 2
    }
}
