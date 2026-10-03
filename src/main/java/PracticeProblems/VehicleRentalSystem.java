import java.util.*;

public class Question2_VehicleRentalSystem {

    // Strategy-like polymorphism: each vehicle calculates its own charge.
    static abstract class Vehicle {
        private final String name;
        private boolean available = true;

        Vehicle(String name) {
            this.name = name;
        }

        abstract double calculateCharge(int days);

        String getName() {
            return name;
        }

        boolean isAvailable() {
            return available;
        }

        void setAvailable(boolean available) {
            this.available = available;
        }
    }

    static class StandardCar extends Vehicle {
        StandardCar(String name) {
            super(name);
        }

        double calculateCharge(int days) {
            return days * 50.0;
        }
    }

    static class LuxuryCar extends Vehicle {
        LuxuryCar(String name) {
            super(name);
        }

        double calculateCharge(int days) {
            return days * 100.0;
        }
    }

    static class SUV extends Vehicle {
        SUV(String name) {
            super(name);
        }

        double calculateCharge(int days) {
            return days * 80.0;
        }
    }

    static class Customer {
        private final String name;

        Customer(String name) {
            this.name = name;
        }
    }

    static class Rental {
        private final Customer customer;
        private final Vehicle vehicle;
        private final int days;
        private final double totalCharge;
        private boolean returned = false;

        Rental(Customer customer, Vehicle vehicle, int days) {
            this.customer = customer;
            this.vehicle = vehicle;
            this.days = days;
            this.totalCharge = vehicle.calculateCharge(days);
        }

        void returnVehicle() {
            if (!returned) {
                returned = true;
                vehicle.setAvailable(true);
                System.out.println(vehicle.getName() + " returned. Now available.");
            }
        }
    }

    static class RentalService {
        private final List<Vehicle> vehicles = new ArrayList<>();
        private final List<Rental> rentals = new ArrayList<>();

        void addVehicle(Vehicle vehicle) {
            vehicles.add(vehicle);
        }

        Rental rentVehicle(Customer customer, String vehicleName, int days) {
            if (days <= 0) {
                System.out.println("Rental failed: Days must be greater than 0.");
                return null;
            }

            for (Vehicle vehicle : vehicles) {
                if (vehicle.getName().equalsIgnoreCase(vehicleName)) {
                    if (!vehicle.isAvailable()) {
                        System.out.println("Rental failed: " + vehicleName +
                                " is currently unavailable.");
                        return null;
                    }

                    vehicle.setAvailable(false);
                    Rental rental = new Rental(customer, vehicle, days);
                    rentals.add(rental);

                    System.out.printf("%s rented for %d days.%n",
                            vehicleName, days);
                    System.out.printf("Total charge: $%.2f%n",
                            vehicle.calculateCharge(days));

                    return rental;
                }
            }

            System.out.println("Rental failed: Vehicle not found.");
            return null;
        }
    }

    public static void main(String[] args) {
        RentalService service = new RentalService();

        service.addVehicle(new LuxuryCar("Luxury Car A"));
        service.addVehicle(new StandardCar("Standard Car B"));
        service.addVehicle(new SUV("SUV C"));

        Customer customer = new Customer("John");

        Rental luxuryRental =
                service.rentVehicle(customer, "Luxury Car A", 3);

        service.rentVehicle(customer, "Standard Car B", 5);

        if (luxuryRental != null) {
            luxuryRental.returnVehicle();
        }

        // The vehicle can be rented again after return.
        service.rentVehicle(customer, "Luxury Car A", 2);
    }
}
