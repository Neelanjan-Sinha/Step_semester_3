public class Main {

    static abstract class LibraryItem {
        private static int nextItemNumber = 1001;
        private final String itemId;

        protected LibraryItem() {
            itemId = "LI-" + nextItemNumber++;
        }

        public abstract int getLoanPeriodDays();

        public String getItemId() {
            return itemId;
        }
    }

    interface Renewable {
        String renew();
    }

    interface Reservable {
        String reserve();
    }

    static class Textbook extends LibraryItem
            implements Renewable, Reservable {

        private final String title;

        public Textbook(String title) {
            super();
            this.title = title;
        }

        @Override
        public int getLoanPeriodDays() {
            return 14;
        }

        @Override
        public String renew() {
            return title + " renewed";
        }

        @Override
        public String reserve() {
            return title + " reserved";
        }
    }

    static class Magazine extends LibraryItem
            implements Renewable {

        private final String title;

        public Magazine(String title) {
            super();
            this.title = title;
        }

        @Override
        public int getLoanPeriodDays() {
            return 7;
        }

        @Override
        public String renew() {
            return title + " renewed";
        }
    }

    static class DigitalPass implements Renewable {
        private final String resourceName;

        public DigitalPass(String resourceName) {
            this.resourceName = resourceName;
        }

        @Override
        public String renew() {
            return resourceName + " renewed";
        }
    }

    static void processCheckouts(LibraryItem[] items) {
        for (LibraryItem item : items) {
            System.out.println(
                    item.getItemId() + ": "
                    + item.getLoanPeriodDays() + " days");
        }
    }

    static String reserveIfSupported(Object o) {
        if (o instanceof Reservable) {
            Reservable r = (Reservable) o;
            return r.reserve();
        }

        return "Reservation not supported";
    }

    public static void main(String[] args) {

        Textbook t = new Textbook("Java Fundamentals");

        System.out.println(t.getLoanPeriodDays());
        System.out.println(t.renew());
        System.out.println(t.reserve());

        Magazine m = new Magazine("Tech Monthly");
        System.out.println(reserveIfSupported(m));

        DigitalPass d = new DigitalPass("E-Journal Access");
        System.out.println(d.renew());
        System.out.println(reserveIfSupported(d));

        // Upcasting: Textbook stored in LibraryItem reference
        LibraryItem ref = t;
        System.out.println(reserveIfSupported(ref));

        processCheckouts(new LibraryItem[]{t, m});
    }
}
