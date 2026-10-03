import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Question3_HotelBookingSystem {

    enum Status {
        ACTIVE, CANCELLED
    }

    static abstract class Room {
        private final String name;
        private final List<Reservation> reservations = new ArrayList<>();

        Room(String name) {
            this.name = name;
        }

        abstract double pricePerNight();

        String getName() {
            return name;
        }

        boolean isAvailable(LocalDate start, LocalDate end) {
            for (Reservation reservation : reservations) {
                if (reservation.getStatus() == Status.ACTIVE &&
                        start.isBefore(reservation.getEnd()) &&
                        end.isAfter(reservation.getStart())) {
                    return false;
                }
            }
            return true;
        }

        void addReservation(Reservation reservation) {
            reservations.add(reservation);
        }
    }

    static class StandardRoom extends Room {
        StandardRoom(String name) {
            super(name);
        }

        double pricePerNight() {
            return 150.0;
        }
    }

    static class DeluxeRoom extends Room {
        DeluxeRoom(String name) {
            super(name);
        }

        double pricePerNight() {
            return 200.0;
        }
    }

    static class Suite extends Room {
        Suite(String name) {
            super(name);
        }

        double pricePerNight() {
            return 300.0;
        }
    }

    static class Customer {
        private final String name;

        Customer(String name) {
            this.name = name;
        }
    }

    static class Reservation {
        private final Room room;
        private final Customer customer;
        private final LocalDate start;
        private final LocalDate end;
        private final double price;
        private Status status = Status.ACTIVE;

        Reservation(Room room, Customer customer,
                    LocalDate start, LocalDate end) {
            this.room = room;
            this.customer = customer;
            this.start = start;
            this.end = end;

            long nights = ChronoUnit.DAYS.between(start, end);
            this.price = nights * room.pricePerNight();
        }

        LocalDate getStart() {
            return start;
        }

        LocalDate getEnd() {
            return end;
        }

        Status getStatus() {
            return status;
        }

        void cancel(LocalDate cancellationDate) {
            if (status == Status.CANCELLED) {
                System.out.println("Reservation is already cancelled.");
                return;
            }

            // Cancellation is allowed before the reservation starts.
            if (cancellationDate.isBefore(start)) {
                status = Status.CANCELLED;
                System.out.println("Reservation for " + room.getName() +
                        " cancelled successfully.");
            } else {
                System.out.println("Cancellation failed: Cancellation deadline passed.");
            }
        }
    }

    static class Hotel {
        private final List<Room> rooms = new ArrayList<>();

        void addRoom(Room room) {
            rooms.add(room);
        }

        Reservation book(Customer customer, String roomName,
                         LocalDate start, LocalDate end) {

            if (!start.isBefore(end)) {
                System.out.println("Booking failed: Invalid date range.");
                return null;
            }

            for (Room room : rooms) {
                if (room.getName().equalsIgnoreCase(roomName)) {
                    if (!room.isAvailable(start, end)) {
                        System.out.println("Booking failed: " + roomName +
                                " is not available for " + start + " to " + end + ".");
                        return null;
                    }

                    Reservation reservation =
                            new Reservation(room, customer, start, end);
                    room.addReservation(reservation);

                    System.out.println(roomName + " booked from " +
                            start + " to " + end + ".");
                    System.out.printf("Total price: $%.2f%n", reservation.price);

                    return reservation;
                }
            }

            System.out.println("Booking failed: Room not found.");
            return null;
        }
    }

    public static void main(String[] args) {
        Hotel hotel = new Hotel();

        hotel.addRoom(new DeluxeRoom("Deluxe Room 101"));
        hotel.addRoom(new StandardRoom("Standard Room 205"));
        hotel.addRoom(new Suite("Suite 301"));

        Customer customer = new Customer("John");

        Reservation deluxe = hotel.book(
                customer,
                "Deluxe Room 101",
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 5));

        hotel.book(
                customer,
                "Standard Room 205",
                LocalDate.of(2024, 12, 3),
                LocalDate.of(2024, 12, 7));

        // This overlaps with the first reservation and should fail.
        hotel.book(
                customer,
                "Deluxe Room 101",
                LocalDate.of(2024, 12, 3),
                LocalDate.of(2024, 12, 7));

        if (deluxe != null) {
            deluxe.cancel(LocalDate.of(2024, 11, 25));
        }
    }
}
