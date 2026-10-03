import java.util.*;

public class Question5_FoodOrderPaymentSystem {

    interface PaymentMethod {
        boolean pay(double amount);
        String getName();
    }

    static class CreditCardPayment implements PaymentMethod {
        public boolean pay(double amount) {
            System.out.printf("Payment via Credit Card successful: $%.2f%n", amount);
            return true;
        }

        public String getName() {
            return "Credit Card";
        }
    }

    static class DigitalWalletPayment implements PaymentMethod {
        public boolean pay(double amount) {
            System.out.printf("Payment via Digital Wallet failed: $%.2f%n", amount);
            return false;
        }

        public String getName() {
            return "Digital Wallet";
        }
    }

    static class CashOnDeliveryPayment implements PaymentMethod {
        public boolean pay(double amount) {
            System.out.printf("Cash on Delivery selected: $%.2f%n", amount);
            return true;
        }

        public String getName() {
            return "Cash on Delivery";
        }
    }

    static class FoodItem {
        private final String name;
        private final double price;

        FoodItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        String getName() {
            return name;
        }

        double getPrice() {
            return price;
        }
    }

    static class LineItem {
        private final FoodItem item;
        private final int quantity;

        LineItem(FoodItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        double getTotal() {
            return item.getPrice() * quantity;
        }

        String getDescription() {
            return item.getName() + " (Qty " + quantity + ")";
        }
    }

    static class Restaurant {
        private final String name;

        Restaurant(String name) {
            this.name = name;
        }
    }

    static class Customer {
        private final String name;

        Customer(String name) {
            this.name = name;
        }
    }

    static class NotificationService {
        void notifyCustomer(String message) {
            System.out.println("Notification: " + message);
        }
    }

    static class Order {
        private static int nextId = 123;

        private final int id;
        private final Customer customer;
        private final Restaurant restaurant;
        private final List<LineItem> items = new ArrayList<>();
        private String status = "Pending Payment";

        Order(Customer customer, Restaurant restaurant) {
            this.id = nextId++;
            this.customer = customer;
            this.restaurant = restaurant;
            System.out.println("Order #" + id + " created.");
        }

        void addItem(FoodItem item, int quantity) {
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0.");
                return;
            }

            items.add(new LineItem(item, quantity));
            System.out.println("Added " + item.getName() +
                    " (Qty " + quantity + ")");
        }

        boolean isEmpty() {
            return items.isEmpty();
        }

        double calculateTotal() {
            double total = 0;

            for (LineItem item : items) {
                total += item.getTotal();
            }

            return total;
        }

        void placeOrder(PaymentMethod payment,
                        NotificationService notification) {

            if (items.isEmpty()) {
                System.out.println(
                        "Cannot place order: Order must contain at least one item.");
                return;
            }

            System.out.println("Order #" + id + " placed.");

            boolean paymentSuccessful = payment.pay(calculateTotal());

            if (paymentSuccessful) {
                status = "Paid";
                System.out.println("Order status: " + status);
                notification.notifyCustomer(
                        "Order #" + id + " placed and paid.");
            } else {
                status = "Pending Payment";
                System.out.println("Order status: " + status);
                notification.notifyCustomer(
                        "Order #" + id + " placed, awaiting payment.");
            }
        }
    }

    public static void main(String[] args) {
        Customer customer = new Customer("John");
        Restaurant restaurant = new Restaurant("Food Corner");
        NotificationService notification = new NotificationService();

        FoodItem pizza = new FoodItem("Pizza", 200);
        FoodItem soda = new FoodItem("Soda", 50);
        FoodItem burger = new FoodItem("Burger", 150);

        // First order
        Order order1 = new Order(customer, restaurant);
        order1.addItem(pizza, 2);
        order1.addItem(soda, 1);

        // Empty order attempt
        Order emptyOrder = new Order(customer, restaurant);
        emptyOrder.placeOrder(
                new CreditCardPayment(), notification);

        // Successful payment
        order1.placeOrder(
                new CreditCardPayment(), notification);

        // Second order with failed payment
        Order order2 = new Order(customer, restaurant);
        order2.addItem(burger, 1);
        order2.placeOrder(
                new DigitalWalletPayment(), notification);
    }
}
