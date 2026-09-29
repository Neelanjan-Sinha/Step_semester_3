// Save as Main.java
class RaceEntry {
    private static final int MAX_FEES = 10;

    private final String bibNumber;
    private final double entryFee;
    private double amountPaid;
    private double totalLateFees;
    private final double[] lateFeeHistory = new double[MAX_FEES];
    private int lateFeeCount;

    public RaceEntry(String bibNumber, double entryFee) {
        if (bibNumber == null || bibNumber.strip().length() < 4) {
            throw new IllegalArgumentException("Invalid bibNumber");
        }
        if (entryFee <= 0) throw new IllegalArgumentException("entryFee must be positive");
        this.bibNumber = bibNumber;
        this.entryFee = entryFee;
    }

    protected String getBibNumber() { return bibNumber; }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    protected void applyLateFee(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (lateFeeCount >= MAX_FEES) throw new IllegalStateException("Late fee limit reached");
        totalLateFees += amount;
        lateFeeHistory[lateFeeCount++] = amount;
    }

    double getBalanceDue() { return entryFee + totalLateFees - amountPaid; }

    double[] getLateFeeHistory() { return java.util.Arrays.copyOf(lateFeeHistory, lateFeeCount); }

    String announce() {
        return "Race Entry | Bib: " + bibNumber + " | Balance: " + getBalanceDue();
    }
}

class RunnerEntry extends RaceEntry {
    private final String category;

    public RunnerEntry(String bibNumber, double entryFee, String category) {
        super(bibNumber, entryFee);
        this.category = category;
    }

    @Override
    protected void applyLateFee(double amount) { super.applyLateFee(amount * 2); }

    @Override
    String announce() {
        return "Runner Entry | Bib: " + getBibNumber() + " | Category: " + category
                + " | Balance: " + getBalanceDue();
    }
}

class RelayTeamEntry extends RaceEntry {
    private final int teamSize;

    public RelayTeamEntry(String bibNumber, double entryFee, int teamSize) {
        super(bibNumber, entryFee);
        if (teamSize <= 0) throw new IllegalArgumentException("teamSize must be positive");
        this.teamSize = teamSize;
    }

    int getTeamSize() { return teamSize; }

    @Override
    String announce() {
        return "Relay Team | Bib: " + getBibNumber() + " | Team Size: " + teamSize
                + " | Balance: " + getBalanceDue();
    }
}

public class Main {
    static String announceAll(RaceEntry[] entries) {
        StringBuilder sb = new StringBuilder();          // one builder for the whole report
        for (RaceEntry e : entries) {
            sb.append(e.announce());                     // polymorphic call
            if (e instanceof RelayTeamEntry) {           // guard, then downcast
                RelayTeamEntry relay = (RelayTeamEntry) e;
                sb.append(" [Team size via downcast: ").append(relay.getTeamSize()).append(']');
            }
            sb.append(" | ");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        RunnerEntry runnerEntry = new RunnerEntry("BIB2001", 80, "Open 10K");
        runnerEntry.pay(30);
        runnerEntry.applyLateFee(20);                    // balance becomes 90.0
        RelayTeamEntry relayEntry = new RelayTeamEntry("BIB4001", 300, 4);

        RaceEntry[] fleet = {runnerEntry, relayEntry};
        System.out.println(announceAll(fleet));
        // Runner Entry | Bib: BIB2001 | Category: Open 10K | Balance: 90.0 | Relay Team | Bib: BIB4001 |
        // Team Size: 4 | Balance: 300.0 [Team size via downcast: 4] |

        // The failure that instanceof avoids:
        try {
            RaceEntry plain = new RaceEntry("BIB5001", 50);
            RelayTeamEntry bad = (RelayTeamEntry) plain;  // compiles, fails at runtime
        } catch (ClassCastException ex) {
            System.out.println("ClassCastException at runtime");
        }
    }
}
