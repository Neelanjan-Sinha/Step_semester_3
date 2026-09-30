public class Main {

    static abstract class PaymentMethod {
        private static int nextTransactionNumber = 1001;
        private final String transactionId;

        protected PaymentMethod() {
            transactionId = "TXN-" + nextTransactionNumber++;
        }

        public abstract String processPayment(double amount);

        // Method overloading
        public String processPayment(double amount, String note) {
            return processPayment(amount) + " (" + note + ")";
        }

        public String getTransactionId() {
            return transactionId;
        }
    }

    static class CreditCardPayment extends PaymentMethod {
        private final String cardNumberLastFour;

        public CreditCardPayment(String cardNumberLastFour) {
            this.cardNumberLastFour = cardNumberLastFour;
        }

        @Override
        public String processPayment(double amount) {
            return "Charged $" + amount + " to card ending "
                    + cardNumberLastFour + " - Txn " + getTransactionId();
        }
    }

    static class CashPayment extends PaymentMethod {

        public CashPayment() {
            super();
        }

        @Override
        public String processPayment(double amount) {
            return "Received $" + amount + " in cash - Txn "
                    + getTransactionId();
        }
    }

    static void printConfirmation(PaymentMethod payment, double amount) {
        System.out.println(payment.processPayment(amount));
    }

    public static void main(String[] args) {

        CreditCardPayment cc = new CreditCardPayment("4471");

        System.out.println(cc.processPayment(250.0));
        System.out.println(cc.processPayment(250.0, "Birthday gift"));

        CashPayment cash = new CashPayment();
        System.out.println(cash.processPayment(40.0));

        // Upcasting: CreditCardPayment object stored in PaymentMethod reference
        PaymentMethod ref = cc;
        printConfirmation(ref, 250.0);

        // new PaymentMethod() cannot compile because PaymentMethod is abstract.
    }
}
