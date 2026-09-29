// Save as Main.java
class EventTicket {
    private static final int MAX_FEES = 10;

    private final double basePrice;
    private double amountPaid;
    private double totalLateFees;
    private final double[] lateFeeHistory = new double[MAX_FEES];   // private audit trail
    private int lateFeeCount;

    public EventTicket(double basePrice) {
        if (basePrice <= 0) throw new IllegalArgumentException("basePrice must be positive");
        this.basePrice = basePrice;
    }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    protected void applyLateFee(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (lateFeeCount >= MAX_FEES) throw new IllegalStateException("Late fee limit reached");
        totalLateFees += amount;
        lateFeeHistory[lateFeeCount++] = amount;   // the only place penalties are recorded
    }

    double getBalanceDue() { return basePrice + totalLateFees - amountPaid; }

    // Defensive copy containing only the recorded entries
    double[] getLateFeeHistory() {
        return java.util.Arrays.copyOf(lateFeeHistory, lateFeeCount);
    }
}

class WorkshopTicket extends EventTicket {
    private final String track;

    public WorkshopTicket(double basePrice) { this(basePrice, "General"); }

    public WorkshopTicket(double basePrice, String track) {
        super(basePrice);
        this.track = track;
    }

    @Override
    protected void applyLateFee(double amount) {
        super.applyLateFee(amount * 2);   // reuse parent's deduction + recording
    }
}

public class Main {
    public static void main(String[] args) {
        WorkshopTicket w = new WorkshopTicket(1200);
        w.pay(1200);
        w.applyLateFee(100);
        System.out.println(w.getBalanceDue());   // 200.0

        double[] history = w.getLateFeeHistory();
        System.out.println(java.util.Arrays.toString(history));            // [200.0]
        history[0] = 999;                                                  // tamper attempt
        System.out.println(java.util.Arrays.toString(w.getLateFeeHistory())); // [200.0]
    }
}
