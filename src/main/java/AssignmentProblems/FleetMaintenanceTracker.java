// Save as Main.java
abstract class ServiceableVehicle {
    private double mileage;                       // private, exposed via JavaBean methods

    public abstract String performMaintenance();

    double getMileage() { return mileage; }

    void addMileage(double km) {
        if (km < 0) throw new IllegalArgumentException("km must be zero or positive");  // reject first
        mileage += km;
    }
}

interface Insurable {
    String getInsuranceInfo();
}

class Forklift extends ServiceableVehicle implements Insurable {
    private final String assetTag;

    public Forklift(String assetTag) { this.assetTag = assetTag; }

    protected String getAssetTag() { return assetTag; }

    @Override
    public String performMaintenance() {
        return "Forklift " + assetTag + ": hydraulic and fork inspection complete";
    }

    @Override
    public String getInsuranceInfo() {
        return "Insured under fleet policy - Asset " + assetTag;
    }
}

class HeavyDutyForklift extends Forklift {
    public HeavyDutyForklift(String assetTag) { super(assetTag); }

    @Override
    public String performMaintenance() {
        return super.performMaintenance() + " | high-pressure hydraulic check complete";
    }
}

// Not insurable, used to show the "no record" branch
class Crane extends ServiceableVehicle {
    @Override
    public String performMaintenance() { return "Crane: cable and boom inspection complete"; }
}

public class Main {
    static String getInsuranceIfApplicable(ServiceableVehicle v) {
        if (v instanceof Insurable) {
            Insurable insurable = (Insurable) v;   // safe: checked first
            return insurable.getInsuranceInfo();
        }
        return "No insurance record exists";
    }

    public static void main(String[] args) {
        Forklift f = new Forklift("FL-22");
        f.addMileage(120);
        System.out.println(f.getMileage());                      // 120.0
        System.out.println(f.performMaintenance());              // Forklift FL-22: hydraulic and fork inspection complete

        HeavyDutyForklift hd = new HeavyDutyForklift("HD-9");
        System.out.println(hd.performMaintenance());
        // Forklift HD-9: hydraulic and fork inspection complete | high-pressure hydraulic check complete

        System.out.println(getInsuranceIfApplicable(f));         // Insured under fleet policy - Asset FL-22
        System.out.println(getInsuranceIfApplicable(new Crane())); // No insurance record exists

        try {
            f.addMileage(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Negative mileage rejected");
        }
    }
}
