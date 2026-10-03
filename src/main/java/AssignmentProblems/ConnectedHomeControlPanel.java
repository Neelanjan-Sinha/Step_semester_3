// Save as Main.java
abstract class HomeDevice {
    private static int counter = 1000;

    private final String serialNumber;

    protected HomeDevice() {
        counter++;
        this.serialNumber = "HD-" + counter;
    }

    public abstract String activate();

    String getSerialNumber() { return serialNumber; }
}

interface RemoteControllable {
    String connect(String appId);
}

interface EnergyTrackable {
    double getConsumptionWatts();
}

class WashingMachine extends HomeDevice implements RemoteControllable, EnergyTrackable {
    private final double consumptionWatts;

    public WashingMachine(double consumptionWatts) { this.consumptionWatts = consumptionWatts; }

    @Override
    public String activate() { return "Washing machine " + getSerialNumber() + " started a cycle"; }

    @Override
    public String connect(String appId) { return getSerialNumber() + " connected to " + appId; }

    @Override
    public double getConsumptionWatts() { return consumptionWatts; }
}

// Sibling of WashingMachine: energy tracking only, not remote-controllable
class Refrigerator extends HomeDevice implements EnergyTrackable {
    private final double consumptionWatts;

    public Refrigerator(double consumptionWatts) { this.consumptionWatts = consumptionWatts; }

    @Override
    public String activate() { return "Refrigerator " + getSerialNumber() + " started cooling"; }

    @Override
    public double getConsumptionWatts() { return consumptionWatts; }
}

// Not a HomeDevice - only remote-controllable
class MobileApp implements RemoteControllable {
    private final String appName;

    public MobileApp(String appName) { this.appName = appName; }

    @Override
    public String connect(String appId) { return appName + " connected to " + appId; }
}

public class Main {
    static void connectAll(RemoteControllable[] items, String appId) {
        for (RemoteControllable item : items) {
            System.out.println(item.connect(appId));
        }
    }

    static double getConsumptionIfTrackable(HomeDevice d) {
        if (d instanceof EnergyTrackable) {
            EnergyTrackable t = (EnergyTrackable) d;   // safe: checked first
            return t.getConsumptionWatts();
        }
        return 0.0;   // not energy trackable
    }

    public static void main(String[] args) {
        WashingMachine wm = new WashingMachine(500.0);
        System.out.println(wm.activate());             // Washing machine HD-1001 started a cycle
        System.out.println(wm.connect("HomeConnect")); // HD-1001 connected to HomeConnect

        Refrigerator fridge = new Refrigerator(150.0);
        System.out.println(getConsumptionIfTrackable(fridge));   // 150.0

        MobileApp app = new MobileApp("HomeConnect App");
        System.out.println(app.connect("HomeConnect"));          // HomeConnect App connected to HomeConnect

        HomeDevice ref = wm;                                     // upcast
        System.out.println(getConsumptionIfTrackable(ref));      // 500.0

        connectAll(new RemoteControllable[]{wm, app}, "HomeConnect");
    }
}
