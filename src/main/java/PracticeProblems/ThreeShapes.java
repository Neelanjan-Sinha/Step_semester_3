// Save as Main.java
class EventTicket {
    private final String attendeeId;
    private final double basePrice;
    private double amountPaid;

    public EventTicket(String attendeeId, double basePrice) {
        if (attendeeId == null || attendeeId.strip().length() < 4) {
            throw new IllegalArgumentException("Invalid attendeeId");
        }
        if (basePrice <= 0) throw new IllegalArgumentException("basePrice must be positive");
        this.attendeeId = attendeeId;
        this.basePrice = basePrice;
    }

    void pay(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        amountPaid += amount;
    }

    double getBalanceDue() { return basePrice - amountPaid; }

    String printTicket() {
        return "Standard Event Ticket | Balance Due: " + getBalanceDue();
    }
}

class WorkshopTicket extends EventTicket {
    private final String track;

    public WorkshopTicket(String attendeeId, double basePrice, String track) {
        super(attendeeId, basePrice);
        this.track = track;
    }

    String getTrack() { return track; }

    @Override
    String printTicket() {
        return "Workshop Ticket | Track: " + track + " | Balance Due: " + getBalanceDue();
    }
}

class PremiumWorkshopTicket extends WorkshopTicket {
    private final double kitFee;

    public PremiumWorkshopTicket(String attendeeId, double basePrice, String track, double kitFee) {
        super(attendeeId, basePrice, track);
        this.kitFee = kitFee;
    }

    @Override
    String printTicket() {
        return "Premium Workshop Ticket | Track: " + getTrack() + " | Kit Fee: " + kitFee
                + " | Balance Due: " + getBalanceDue();
    }
}

class HackathonTicket extends EventTicket {
    private final String teamName;

    public HackathonTicket(String attendeeId, double basePrice, String teamName) {
        super(attendeeId, basePrice);
        this.teamName = teamName;
    }

    @Override
    String printTicket() {
        return "Hackathon Ticket | Team: " + teamName + " | Balance Due: " + getBalanceDue();
    }
}

public class Main {
    // instanceof only; most specific type is checked first
    static String classifyGeneration(EventTicket ticket) {
        if (ticket instanceof PremiumWorkshopTicket) return "Multilevel descendant (3 generations deep)";
        if (ticket instanceof HackathonTicket)       return "Hierarchical sibling (independent branch)";
        if (ticket instanceof WorkshopTicket)        return "Single-inheritance child (2 generations deep)";
        return "Base class (root of the hierarchy)";
    }

    // Pure polymorphism - no type checks
    static double getTotalBalanceDue(EventTicket[] tickets) {
        double total = 0;
        for (EventTicket t : tickets) total += t.getBalanceDue();
        return total;
    }

    public static void main(String[] args) {
        EventTicket standard = new EventTicket("STU1", 500);
        WorkshopTicket workshop = new WorkshopTicket("STU2", 1200, "AI/ML");
        PremiumWorkshopTicket premium = new PremiumWorkshopTicket("STU3", 2000, "Cloud Native", 300);
        HackathonTicket hack = new HackathonTicket("STU4", 800, "Byte Force");

        System.out.println(standard.printTicket());
        System.out.println(workshop.printTicket());
        System.out.println(premium.printTicket());
        System.out.println(hack.printTicket());

        System.out.println(classifyGeneration(premium));
        System.out.println(classifyGeneration(hack));

        System.out.println(getTotalBalanceDue(new EventTicket[]{standard, workshop, premium, hack})); // 4500.0
    }
}
