// Save as Main.java
class RaceEntry {
    private final String bibNumber;
    private final double entryFee;
    private double amountPaid;

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

    double getBalanceDue() { return entryFee - amountPaid; }

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

    String getCategory() { return category; }

    @Override
    String announce() {
        return "Runner Entry | Bib: " + getBibNumber() + " | Category: " + category
                + " | Balance: " + getBalanceDue();
    }
}

class EliteRunnerEntry extends RunnerEntry {
    private final double sponsorBonus;

    public EliteRunnerEntry(String bibNumber, double entryFee, String category, double sponsorBonus) {
        super(bibNumber, entryFee, category);
        this.sponsorBonus = sponsorBonus;
    }

    @Override
    String announce() {
        return "Elite Runner | Bib: " + getBibNumber() + " | Category: " + getCategory()
                + " | Sponsor Bonus: " + sponsorBonus + " | Balance: " + getBalanceDue();
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
    // instanceof only; most specific type first
    static String classifyGeneration(RaceEntry entry) {
        if (entry instanceof EliteRunnerEntry) return "Multilevel descendant (3 generations deep)";
        if (entry instanceof RelayTeamEntry)   return "Hierarchical sibling (independent branch)";
        if (entry instanceof RunnerEntry)      return "Single-inheritance child (2 generations deep)";
        return "Base class (root of the hierarchy)";
    }

    // Pure polymorphism, no type checks
    static double getTotalBalanceDue(RaceEntry[] entries) {
        double total = 0;
        for (RaceEntry e : entries) total += e.getBalanceDue();
        return total;
    }

    public static void main(String[] args) {
        RunnerEntry runner = new RunnerEntry("BIB2001", 80, "Open 10K");
        EliteRunnerEntry elite = new EliteRunnerEntry("BIB3001", 150, "Elite Full Marathon", 500);
        RelayTeamEntry relay = new RelayTeamEntry("BIB4001", 300, 4);

        System.out.println(runner.announce());
        System.out.println(elite.announce());
        System.out.println(relay.announce());

        System.out.println(classifyGeneration(elite));   // Multilevel descendant (3 generations deep)
        System.out.println(classifyGeneration(relay));   // Hierarchical sibling (independent branch)

        System.out.println(getTotalBalanceDue(new RaceEntry[]{runner, elite, relay}));   // 530.0
    }
}
