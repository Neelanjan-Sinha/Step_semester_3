import java.util.*;
 
final class Money {
    private Money() {}
    static long rupees(long r) { return r * 100; }
    static String plain(long paise) { long a = Math.abs(paise); return (a / 100) + "." + (a % 100 < 10 ? "0" : "") + (a % 100); }
    static String fmt(long paise) { return "\u20B9" + plain(paise); }
    static String signed(long paise) { return (paise >= 0 ? "+" : "-") + plain(paise); }
}
 
class CardException extends RuntimeException {
    CardException(String message) { super(message); }
}
 
interface PricingPlan {
    String getName();
    long finalPrice(long listPricePaise);
}
 
abstract class DiscountPlan implements PricingPlan {
    private final String name;
    private final int discountPercent;
 
    protected DiscountPlan(String name, int discountPercent) {
        this.name = name;
        this.discountPercent = discountPercent;
    }
 
    public String getName() { return name; }
 
    public long finalPrice(long listPricePaise) {
        return (listPricePaise * (100 - discountPercent) + 50) / 100;   // rounded half-up to the paisa
    }
}
 
class DayScholarPlan extends DiscountPlan { DayScholarPlan() { super("Day Scholar", 0); } }
class HostellerPlan extends DiscountPlan { HostellerPlan() { super("Hosteller", 10); } }
class StaffPlan extends DiscountPlan { StaffPlan() { super("Staff", 20); } }
 
enum TransactionType { TOPUP, PURCHASE, REFUND }
 
final class Transaction {
    private final TransactionType type;
    private final long amountPaise;          // signed: + adds to balance, - reduces it
    private final String description;
 
    Transaction(TransactionType type, long amountPaise, String description) {
        this.type = type;
        this.amountPaise = amountPaise;
        this.description = description;
    }
 
    TransactionType getType() { return type; }
    long getAmount() { return amountPaise; }
    String getDescription() { return description; }
}
 
class SmartCard {
    private static final long MIN_TOPUP = Money.rupees(100);
    private static final long MAX_BALANCE = Money.rupees(5000);
 
    private final String cardId;
    private final PricingPlan plan;
    private boolean active = true;
    private long balance;                                             // private, changed only in record()
    private final List<Transaction> ledger = new ArrayList<>();
    private final Set<Transaction> refundedPurchases = new HashSet<>();   // identity-based (no equals override)
 
    SmartCard(String cardId, PricingPlan plan) {
        this.cardId = cardId;
        this.plan = plan;
    }
 
    String getCardId() { return cardId; }
    long getBalance() { return balance; }
    boolean isActive() { return active; }
 
    void block() { active = false; }
    void unblock() { active = true; }
 
    Transaction topUp(long amountPaise) {
        requireActive();
        if (amountPaise < MIN_TOPUP) throw new CardException("Minimum top-up is " + Money.fmt(MIN_TOPUP));
        if (balance + amountPaise > MAX_BALANCE) {
            throw new CardException("Balance cannot exceed " + Money.fmt(MAX_BALANCE));
        }
        return record(TransactionType.TOPUP, amountPaise, "Top-up");
    }
 
    Transaction purchase(String item, long listPricePaise) {
        requireActive();
        if (listPricePaise <= 0) throw new CardException("Price must be positive");
        long charged = plan.finalPrice(listPricePaise);
        if (charged > balance) {
            throw new CardException("Insufficient balance (required " + Money.fmt(charged)
                    + ", available " + Money.fmt(balance) + ")");
        }
        return record(TransactionType.PURCHASE, -charged, item);
    }
 
    Transaction refund(Transaction purchase) {
        if (purchase == null || purchase.getType() != TransactionType.PURCHASE || !ledger.contains(purchase)) {
            throw new CardException("Not a purchase made with this card");
        }
        if (refundedPurchases.contains(purchase)) {
            throw new CardException(purchase.getDescription() + " has already been refunded");
        }
        refundedPurchases.add(purchase);
        return record(TransactionType.REFUND, -purchase.getAmount(), purchase.getDescription());
    }
 
    String miniStatement() {
        StringJoiner sj = new StringJoiner(", ");
        for (Transaction t : ledger) sj.add(Money.signed(t.getAmount()));
        return "Mini-statement for " + cardId + ": " + sj + " = " + Money.fmt(balance);
    }
 
    // Proof helper: recompute the balance from the ledger and compare with the stored balance
    boolean isConsistent() {
        long sum = 0;
        for (Transaction t : ledger) sum += t.getAmount();
        return sum == balance && balance >= 0;
    }
 
    private void requireActive() {
        if (!active) throw new CardException("Card " + cardId + " is blocked");
    }
 
    // The ONLY place where the balance changes - ledger and balance are updated together
    private Transaction record(TransactionType type, long signedAmount, String description) {
        Transaction t = new Transaction(type, signedAmount, description);
        ledger.add(t);
        balance += signedAmount;
        return t;
    }
}
 
public class Main {
    static void purchase(SmartCard card, String item, long listPrice) {
        try {
            Transaction t = card.purchase(item, listPrice);
            System.out.println(t.getDescription() + " purchased for " + Money.fmt(-t.getAmount())
                    + ". Balance: " + Money.fmt(card.getBalance()) + ".");
        } catch (CardException e) {
            System.out.println("Purchase failed: " + e.getMessage() + ".");
        }
    }
 
    public static void main(String[] args) {
        SmartCard card = new SmartCard("C-2045", new HostellerPlan());
 
        card.topUp(Money.rupees(500));
        System.out.println("C-2045 topped up with " + Money.fmt(Money.rupees(500))
                + ". Balance: " + Money.fmt(card.getBalance()) + ".");
 
        // keep the Veg Thali transaction so it can be refunded later
        Transaction thali = card.purchase("Veg Thali", Money.rupees(120));
        System.out.println("Veg Thali purchased for " + Money.fmt(-thali.getAmount())
                + ". Balance: " + Money.fmt(card.getBalance()) + ".");
 
        purchase(card, "Cold Coffee", Money.rupees(60));
        purchase(card, "Items worth 400", Money.rupees(400));      // fails: needs 360.00, has 338.00
 
        Transaction refund = card.refund(thali);
        System.out.println("Refund of " + Money.fmt(refund.getAmount()) + " for " + refund.getDescription()
                + " processed. Balance: " + Money.fmt(card.getBalance()) + ".");
 
        try {
            card.refund(thali);
        } catch (CardException e) {
            System.out.println("Refund rejected: " + e.getMessage() + ".");
        }
 
        System.out.println(card.miniStatement());
 
        // --- extra demo (not in the sample): blocking, and the invariant check ---
        card.block();
        purchase(card, "Tea", Money.rupees(20));                    // rejected: card is blocked
        card.unblock();
        System.out.println("Balance equals sum of transactions: " + card.isConsistent());
    }
}
