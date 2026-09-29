// Save as Main.java
class RaceEntry {
    private static int bibCounter = 0;           // shared across all entries

    public final String entryCode;               // final: assigned once, never reassigned
    private final String bibNumber;
    private final double entryFee;
    private double amountPaid;

    public RaceEntry(String bibNumber, double entryFee) {
        // Validate BEFORE touching the counter so rejected constructions never count
        if (bibNumber == null || bibNumber.strip().length() < 4) {
            throw new IllegalArgumentException("Invalid bibNumber");
        }
        if (entryFee <= 0) throw new IllegalArgumentException("entryFee must be positive");
        bibCounter++;                            // once per object (subclasses go through super(...))
        this.entryCode = "ENT-" + bibCounter;
        this.bibNumber = bibNumber;
        this.entryFee = entryFee;
    }

    protected String getBibNumber() { return bibNumber; }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    void pay(double amount, String mode) {
        System.out.println("Paying via " + mode);
        pay(amount);                             // reuse the flat version
    }

    double getBalanceDue() { return entryFee - amountPaid; }

    static boolean isValidDiscountCode(String code) {
        if (code == null || code.length() != 5) return false;   // length first
        return code.charAt(0) == 'M'
                && Character.isDigit(code.charAt(1))
                && Character.isDigit(code.charAt(2))
                && Character.isDigit(code.charAt(3))
                && Character.isUpperCase(code.charAt(4));
    }

    static int getBibCounter() { return bibCounter; }
}

class RunnerEntry extends RaceEntry {
    private final String category;

    public RunnerEntry(String bibNumber, double entryFee, String category) {
        super(bibNumber, entryFee);
        this.category = category;
    }
}

class EliteRunnerEntry extends RunnerEntry {
    private final double sponsorBonus;

    public EliteRunnerEntry(String bibNumber, double entryFee, String category, double sponsorBonus) {
        super(bibNumber, entryFee, category);
        this.sponsorBonus = sponsorBonus;
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
}

public class Main {
    static String settleNight(RaceEntry[] entries) {
        int processed = 0, nulls = 0, relays = 0, individuals = 0;
        for (RaceEntry e : entries) {
            if (e == null) { nulls++; continue; }
            processed++;
            if (e instanceof RelayTeamEntry) relays++;
            else individuals++;
        }
        return processed + " processed | " + nulls + " null skipped | "
                + relays + " relay | " + individuals + " individual";
    }

    public static void main(String[] args) {
        RaceEntry r = new RunnerEntry("BIB2001", 80, "Open 10K");
        EliteRunnerEntry eliteEntry = new EliteRunnerEntry("BIB3001", 150, "Elite Full Marathon", 500);
        RelayTeamEntry relayEntry = new RelayTeamEntry("BIB4001", 300, 4);
        new RaceEntry("BIB5001", 50);

        System.out.println(RaceEntry.isValidDiscountCode("M123A")); // true
        System.out.println(RaceEntry.isValidDiscountCode("M12A"));  // false
        System.out.println(RaceEntry.isValidDiscountCode("X123A")); // false

        r.pay(10, "UPI");                                           // Paying via UPI
        System.out.println(settleNight(new RaceEntry[]{eliteEntry, null, relayEntry}));
        // 2 processed | 1 null skipped | 1 relay | 1 individual

        System.out.println(RaceEntry.getBibCounter());              // 4
    }
}
