// Save as Main.java
class EventTicket {
    private final double basePrice;
    private double amountPaid;

    public EventTicket(double basePrice) {
        if (basePrice <= 0) throw new IllegalArgumentException("basePrice must be positive");
        this.basePrice = basePrice;
    }

    void pay(double amount) { amountPaid += amount; }
    double getBalanceDue() { return basePrice - amountPaid; }

    String printTicket() {
        return "Standard | Balance: " + getBalanceDue();
    }
}

class WorkshopTicket extends EventTicket {
    private final String track;

    public WorkshopTicket(double basePrice, String track) {
        super(basePrice);
        this.track = track;
    }

    String getTrack() { return track; }

    @Override
    String printTicket() {
        return "Workshop | Track: " + track + " | Balance: " + getBalanceDue();
    }
}

public class Main {
    static String batchPrint(EventTicket[] tickets) {
        StringBuilder sb = new StringBuilder();          // one builder for the whole report
        for (EventTicket t : tickets) {
            sb.append(t.printTicket());                  // polymorphic call
            if (t instanceof WorkshopTicket) {           // guard, then downcast
                WorkshopTicket w = (WorkshopTicket) t;
                sb.append(" [Track via downcast: ").append(w.getTrack()).append(']');
            }
            sb.append(" | ");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(batchPrint(new EventTicket[]{
                new EventTicket(500), new WorkshopTicket(1200, "AI/ML")}));
        // Standard | Balance: 500.0 | Workshop | Track: AI/ML | Balance: 1200.0 [Track via downcast: AI/ML] |

        // Demonstrates the failure that instanceof avoids:
        try {
            EventTicket plain = new EventTicket(500);
            WorkshopTicket bad = (WorkshopTicket) plain;   // compiles, fails at runtime
        } catch (ClassCastException e) {
            System.out.println("ClassCastException at runtime");
        }
    }
}
