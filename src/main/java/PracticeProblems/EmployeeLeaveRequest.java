import java.time.LocalDate;

public class Question4_EmployeeLeaveManagement {

    enum LeaveStatus {
        PENDING, APPROVED, REJECTED
    }

    // Abstract employee type allows different leave policies.
    static abstract class Employee {
        private final String name;

        Employee(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        abstract boolean isLeaveAllowed(LocalDate start, LocalDate end);
    }

    static class FullTimeEmployee extends Employee {
        FullTimeEmployee(String name) {
            super(name);
        }

        boolean isLeaveAllowed(LocalDate start, LocalDate end) {
            return true;
        }
    }

    static class PartTimeEmployee extends Employee {
        PartTimeEmployee(String name) {
            super(name);
        }

        boolean isLeaveAllowed(LocalDate start, LocalDate end) {
            long days = end.toEpochDay() - start.toEpochDay() + 1;
            return days <= 5;
        }
    }

    static class ContractEmployee extends Employee {
        ContractEmployee(String name) {
            super(name);
        }

        boolean isLeaveAllowed(LocalDate start, LocalDate end) {
            long days = end.toEpochDay() - start.toEpochDay() + 1;
            return days <= 10;
        }
    }

    static class LeaveRequest {
        private final Employee employee;
        private final LocalDate start;
        private final LocalDate end;
        private LeaveStatus status = LeaveStatus.PENDING;

        LeaveRequest(Employee employee, LocalDate start, LocalDate end) {
            this.employee = employee;
            this.start = start;
            this.end = end;
        }

        void approve() {
            if (status != LeaveStatus.PENDING) {
                System.out.println("Cannot approve: Request is already " + status + ".");
                return;
            }

            if (!employee.isLeaveAllowed(start, end)) {
                status = LeaveStatus.REJECTED;
                System.out.println("Leave request for " + employee.getName() +
                        " rejected due to leave policy.");
                return;
            }

            status = LeaveStatus.APPROVED;
            System.out.println("Leave request for " + employee.getName() +
                    " approved. Status: " + status);
        }

        void reject() {
            if (status != LeaveStatus.PENDING) {
                System.out.println("Cannot reject: Request is already " + status + ".");
                return;
            }

            status = LeaveStatus.REJECTED;
            System.out.println("Leave request for " + employee.getName() +
                    " rejected. Status: " + status);
        }

        void changeToPending() {
            if (status != LeaveStatus.PENDING) {
                System.out.println("Cannot change status: " + status +
                        " request cannot revert to Pending.");
                return;
            }

            System.out.println("Request is already Pending.");
        }
    }

    static class LeaveManager {
        LeaveRequest submitLeave(Employee employee,
                                  LocalDate start, LocalDate end) {
            if (!start.isBefore(end) && !start.equals(end)) {
                System.out.println("Invalid leave dates.");
                return null;
            }

            LeaveRequest request =
                    new LeaveRequest(employee, start, end);

            System.out.println("Leave request submitted by " +
                    employee.getName() + " for " + start + " to " +
                    end + ". Status: Pending.");

            return request;
        }
    }

    public static void main(String[] args) {
        LeaveManager manager = new LeaveManager();

        Employee john = new FullTimeEmployee("John Doe");
        Employee jane = new PartTimeEmployee("Jane Smith");

        LeaveRequest johnRequest = manager.submitLeave(
                john,
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 12));

        if (johnRequest != null) {
            johnRequest.approve();
            johnRequest.changeToPending();
        }

        manager.submitLeave(
                jane,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 5));
    }
}
