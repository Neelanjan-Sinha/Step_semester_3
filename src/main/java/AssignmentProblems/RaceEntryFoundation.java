// Save as Main.java
class RaceEntry {
    private final String bibNumber;
    private final double entryFee;
    private double amountPaid;

    public RaceEntry(String bibNumber, double entryFee) {
        // The only place the validation rule lives
        if (bibNumber == null || bibNumber.strip().length() < 4) {
            throw new IllegalArgumentException("bibNumber must be non-blank and at least 4 characters");
        }
        if (entryFee <= 0) throw new IllegalArgumentException("entryFee must be positive");
        this.bibNumber = bibNumber;
        this.entryFee = entryFee;
    }

    protected String getBibNumber() { return bibNumber; }
    protected double getEntryFee()  { return entryFee; }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    double getBalanceDue() { return entryFee - amountPaid; }

    static String registerBatch(String[] bibNumbers, double entryFee) {
        int registered = 0, rejected = 0;
        for (String bib : bibNumbers) {
            try {
                new RaceEntry(bib, entryFee);
                registered++;
            } catch (IllegalArgumentException e) {
                rejected++;
            }
        }
        return "Registered: " + registered + " | Rejected: " + rejected;
    }
}

class RunnerEntry extends RaceEntry {
    private final String category;

    public RunnerEntry(String bibNumber, double entryFee, String category) {
        super(bibNumber, entryFee);   // no duplicated fields
        this.category = category;
    }

    String getCategory() { return category; }
}

public class Main {
    public static void main(String[] args) {
        try {
            new RaceEntry("B1", 50);
        } catch (IllegalArgumentException e) {
            System.out.println("construction rejected");
        }

        RunnerEntry r = new RunnerEntry("BIB2001", 80, "Open 10K");
        r.pay(30);
        System.out.println(r.getBalanceDue());   // 50.0

        System.out.println(RaceEntry.registerBatch(new String[]{"BIB1", "B1", "BIB2"}, 80));
        // Registered: 2 | Rejected: 1
    }
}
