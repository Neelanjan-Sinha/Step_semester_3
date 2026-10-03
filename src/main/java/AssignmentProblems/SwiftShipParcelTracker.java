import java.util.*;
 
enum ParcelStatus {
    BOOKED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED;
 
    // Only the very next status in the chain is allowed; CANCELLED is reached via Parcel.cancel() only
    boolean canMoveTo(ParcelStatus target) {
        return this != CANCELLED && target != CANCELLED && target.ordinal() == this.ordinal() + 1;
    }
}
 
interface ShippingType {
    String getName();
    double calculateCharge(double weightKg);
}
 
class StandardShipping implements ShippingType {
    public String getName() { return "Standard"; }
    public double calculateCharge(double weightKg) { return 40 + 10 * weightKg; }
}
 
class ExpressShipping implements ShippingType {
    public String getName() { return "Express"; }
    public double calculateCharge(double weightKg) { return 80 + 15 * weightKg; }
}
 
class FragileShipping implements ShippingType {
    private static final double HANDLING_FEE = 50;
    private final ShippingType base;
 
    FragileShipping() { this(new StandardShipping()); }
    FragileShipping(ShippingType base) { this.base = base; }
 
    public String getName() { return "Fragile"; }
    public double calculateCharge(double weightKg) { return base.calculateCharge(weightKg) + HANDLING_FEE; }
}
 
interface NotificationChannel {
    void send(String message);
}
 
class SmsChannel implements NotificationChannel {
    public void send(String message) { System.out.println("[SMS] " + message); }
}
 
class EmailChannel implements NotificationChannel {
    public void send(String message) { System.out.println("[Email] " + message); }
}
 
class Customer {
    private final String name;
    private final List<Parcel> parcels = new ArrayList<>();
 
    Customer(String name) { this.name = name; }
 
    String getName() { return name; }
    List<Parcel> getParcels() { return Collections.unmodifiableList(parcels); }
    void addParcel(Parcel p) { parcels.add(p); }
}
 
class Parcel {
    private final String id;
    private final Customer customer;
    private final ShippingType shippingType;
    private final double weightKg;
    private final double charge;
    private ParcelStatus status = ParcelStatus.BOOKED;
    private final List<NotificationChannel> channels = new ArrayList<>();
 
    Parcel(String id, Customer customer, ShippingType shippingType, double weightKg) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Parcel id is blank");
        if (weightKg <= 0) throw new IllegalArgumentException("Weight must be positive");
        this.id = id;
        this.customer = customer;
        this.shippingType = shippingType;
        this.weightKg = weightKg;
        this.charge = shippingType.calculateCharge(weightKg);
    }
 
    String getId() { return id; }
    ShippingType getShippingType() { return shippingType; }
    double getWeightKg() { return weightKg; }
    double getCharge() { return charge; }
    ParcelStatus getStatus() { return status; }
 
    void subscribe(NotificationChannel channel) { channels.add(channel); }
 
    // Status can only change through these two methods, so it can never become invalid
    void advanceTo(ParcelStatus target) {
        if (!status.canMoveTo(target)) {
            throw new IllegalStateException("Invalid transition: " + status + " \u2192 " + target + " is not allowed");
        }
        status = target;
        notifyChannels();
    }
 
    void cancel() {
        if (status != ParcelStatus.BOOKED) {
            throw new IllegalStateException(id + " can be cancelled only while BOOKED");
        }
        status = ParcelStatus.CANCELLED;
        notifyChannels();
    }
 
    void announceCurrentStatus() { notifyChannels(); }
 
    private void notifyChannels() {
        String message = id + " is now " + status + ".";
        for (NotificationChannel c : channels) c.send(message);
    }
}
 
class ParcelService {
    private final Map<String, Parcel> parcels = new HashMap<>();
 
    Parcel bookParcel(Customer customer, String id, ShippingType type, double weightKg,
                      NotificationChannel... channels) {
        if (parcels.containsKey(id)) throw new IllegalArgumentException("Parcel " + id + " already exists");
        Parcel p = new Parcel(id, customer, type, weightKg);
        for (NotificationChannel c : channels) p.subscribe(c);
        parcels.put(id, p);
        customer.addParcel(p);
 
        System.out.println("Parcel " + id + " booked (" + type.getName() + ", " + formatWeight(weightKg) + " kg). "
                + "Charge: \u20B9" + String.format(Locale.US, "%.2f", p.getCharge()) + ".");
        p.announceCurrentStatus();
        return p;
    }
 
    void advance(String id, ParcelStatus target) { find(id).advanceTo(target); }
 
    void cancel(String id) { find(id).cancel(); }
 
    private Parcel find(String id) {
        Parcel p = parcels.get(id);
        if (p == null) throw new IllegalArgumentException("Unknown parcel " + id);
        return p;
    }
 
    private static String formatWeight(double w) {
        return w == Math.floor(w) ? String.valueOf((long) w) : String.valueOf(w);
    }
}
 
public class Main {
    public static void main(String[] args) {
        ParcelService service = new ParcelService();
        Customer customer = new Customer("Meera");
 
        service.bookParcel(customer, "P101", new ExpressShipping(), 2, new SmsChannel(), new EmailChannel());
        service.advance("P101", ParcelStatus.PICKED_UP);
 
        try {
            service.cancel("P101");
        } catch (IllegalStateException e) {
            System.out.println("Cancellation failed: " + e.getMessage() + ".");
        }
 
        service.advance("P101", ParcelStatus.IN_TRANSIT);
 
        try {
            service.advance("P101", ParcelStatus.DELIVERED);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage() + ".");
        }
    }
}
