// Save as Main.java
class RaceEntry {
    private static final int MAX_FEES = 10;

    private final String bibNumber;
    private final double entryFee;
    private double amountPaid;
    private double totalLateFees;
    private final double[] lateFeeHistory = new double[MAX_FEES];   // private audit trail
    private int lateFeeCount;

    public RaceEntry(String bibNumber, double entryFee) {
        if (bibNumber == null || bibNumber.strip().length() < 4) {
            throw new IllegalArgumentException("Invalid bibNumber");
        }
        if (entryFee <= 0) throw new IllegalArgumentException("entryFee must be positive");
        this.bibNumber = bibNumber;
        this.entryFee = entryFee;
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

    double getBalanceDue() { return entryFee + totalLateFees - amountPaid; }

    // Defensive copy of only the recorded entries
    double[] getLateFeeHistory() {
        return java.util.Arrays.copyOf(lateFeeHistory, lateFeeCount);
    }
}

class RunnerEntry extends RaceEntry {
    private final String category;

    public RunnerEntry(String bibNumber, double entryFee, String category) {
        super(bibNumber, entryFee);
        this.category = category;
    }

    @Override
    protected void applyLateFee(double amount) {
        super.applyLateFee(amount * 2);   // reuse parent's deduction + recording
    }
}

public class Main {
    public static void main(String[] args) {
        RunnerEntry r = new RunnerEntry("BIB2001", 80, "Open 10K");
        r.pay(30);
        r.applyLateFee(20);
        System.out.println(r.getBalanceDue());   // 90.0

        double[] history = r.getLateFeeHistory();
        System.out.println(java.util.Arrays.toString(history));               // [40.0]
        history[0] = 999;                                                     // tamper attempt
        System.out.println(java.util.Arrays.toString(r.getLateFeeHistory())); // [40.0]
    }
}
