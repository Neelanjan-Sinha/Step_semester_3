import java.util.*;
 
class EnrollmentException extends RuntimeException {
    EnrollmentException(String message) { super(message); }
}
 
interface CreditPolicy {
    String getName();
    int getMaxCredits();
}
 
class RegularPolicy implements CreditPolicy {
    public String getName() { return "Regular"; }
    public int getMaxCredits() { return 24; }
}
 
class HonorsPolicy implements CreditPolicy {
    public String getName() { return "Honors"; }
    public int getMaxCredits() { return 28; }
}
 
class ExchangePolicy implements CreditPolicy {
    public String getName() { return "Exchange"; }
    public int getMaxCredits() { return 20; }
}
 
class Student {
    private final String name;
    private final CreditPolicy policy;
    private int credits;
    private final Set<Elective> electives = new LinkedHashSet<>();
 
    Student(String name, CreditPolicy policy, int currentCredits) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Student name is blank");
        if (currentCredits < 0) throw new IllegalArgumentException("Credits cannot be negative");
        this.name = name;
        this.policy = policy;
        this.credits = currentCredits;
    }
 
    String getName() { return name; }
    CreditPolicy getPolicy() { return policy; }
    int getCredits() { return credits; }
    int getCreditLimit() { return policy.getMaxCredits(); }
 
    boolean canTake(int extraCredits) { return credits + extraCredits <= policy.getMaxCredits(); }
 
    // Package-private: only Elective changes a student's enrollment state
    void recordEnrollment(Elective e) {
        electives.add(e);
        credits += e.getCredits();
    }
 
    void recordDrop(Elective e) {
        electives.remove(e);
        credits -= e.getCredits();
    }
}
 
class Enrollment {
    private final Student student;
    private final Elective elective;
 
    Enrollment(Student student, Elective elective) {
        this.student = student;
        this.elective = elective;
    }
 
    Student getStudent() { return student; }
    Elective getElective() { return elective; }
}
 
class Elective {
    enum Outcome { ENROLLED, WAITLISTED }
 
    private final String name;
    private final int credits;
    private final int capacity;
    private final Map<Student, Enrollment> enrollments = new LinkedHashMap<>();   // size <= capacity
    private final Deque<Student> waitlist = new ArrayDeque<>();                   // private FIFO queue
 
    Elective(String name, int credits, int capacity) {
        if (credits <= 0 || capacity <= 0) throw new IllegalArgumentException("Credits and capacity must be positive");
        this.name = name;
        this.credits = credits;
        this.capacity = capacity;
    }
 
    String getName() { return name; }
    int getCredits() { return credits; }
    int getEnrolledCount() { return enrollments.size(); }
    boolean isEnrolled(Student s) { return enrollments.containsKey(s); }
 
    int getWaitlistPosition(Student s) {
        int pos = 0;
        for (Student w : waitlist) {
            pos++;
            if (w == s) return pos;
        }
        return -1;
    }
 
    Outcome request(Student s) {
        if (isEnrolled(s)) throw new EnrollmentException(s.getName() + " is already enrolled in " + name);
        if (getWaitlistPosition(s) > 0) throw new EnrollmentException(s.getName() + " is already waitlisted for " + name);
 
        if (!s.canTake(credits)) {                       // credit limit BEFORE seat availability
            throw new EnrollmentException(s.getName() + " would exceed the " + s.getPolicy().getName()
                    + " credit limit (" + (s.getCredits() + credits) + "/" + s.getCreditLimit() + ")");
        }
        if (enrollments.size() < capacity) {
            admit(s);
            return Outcome.ENROLLED;
        }
        waitlist.addLast(s);
        return Outcome.WAITLISTED;
    }
 
    // Drops the student and promotes the first ELIGIBLE waitlisted student. Returns the promoted student or null.
    Student drop(Student s) {
        Enrollment e = enrollments.remove(s);
        if (e == null) throw new EnrollmentException(s.getName() + " is not enrolled in " + name);
        s.recordDrop(this);
        return promoteNext();
    }
 
    boolean withdrawFromWaitlist(Student s) { return waitlist.remove(s); }
 
    private Student promoteNext() {
        if (enrollments.size() >= capacity) return null;          // invariant guard
        Iterator<Student> it = waitlist.iterator();
        while (it.hasNext()) {
            Student candidate = it.next();
            if (candidate.canTake(credits)) {                     // credit limit re-checked on promotion
                it.remove();
                admit(candidate);
                return candidate;
            }
        }
        return null;
    }
 
    private void admit(Student s) {
        enrollments.put(s, new Enrollment(s, this));
        s.recordEnrollment(this);
    }
}
 
class EnrollmentService {
    void enroll(Student s, Elective e) {
        try {
            Elective.Outcome outcome = e.request(s);
            if (outcome == Elective.Outcome.ENROLLED) {
                System.out.println(s.getName() + " enrolled in " + e.getName()
                        + " (credits: " + s.getCredits() + "/" + s.getCreditLimit() + ").");
            } else {
                System.out.println(e.getName() + " is full. " + s.getName()
                        + " added to waitlist (position " + e.getWaitlistPosition(s) + ").");
            }
        } catch (EnrollmentException ex) {
            System.out.println("Enrollment failed: " + ex.getMessage() + ".");
        }
    }
 
    void drop(Student s, Elective e) {
        try {
            Student promoted = e.drop(s);
            System.out.println(s.getName() + " dropped " + e.getName()
                    + " (credits: " + s.getCredits() + "/" + s.getCreditLimit() + ").");
            if (promoted != null) {
                System.out.println(promoted.getName() + " promoted from waitlist and enrolled in " + e.getName()
                        + " (credits: " + promoted.getCredits() + "/" + promoted.getCreditLimit() + ").");
            }
        } catch (EnrollmentException ex) {
            System.out.println("Drop failed: " + ex.getMessage() + ".");
        }
    }
 
    void withdrawFromWaitlist(Student s, Elective e) {
        if (e.withdrawFromWaitlist(s)) {
            System.out.println(s.getName() + " removed from the waitlist of " + e.getName() + ".");
        } else {
            System.out.println("Withdraw failed: " + s.getName() + " is not on the waitlist of " + e.getName() + ".");
        }
    }
}
 
public class Main {
    public static void main(String[] args) {
        EnrollmentService service = new EnrollmentService();
        Elective cloud = new Elective("Cloud Computing", 4, 2);
 
        Student asha = new Student("Asha", new RegularPolicy(), 20);
        Student ravi = new Student("Ravi", new HonorsPolicy(), 22);
        Student neha = new Student("Neha", new ExchangePolicy(), 12);
        Student kiran = new Student("Kiran", new RegularPolicy(), 22);
 
        service.enroll(asha, cloud);
        service.enroll(ravi, cloud);
        service.enroll(neha, cloud);
        service.enroll(kiran, cloud);
        service.drop(asha, cloud);
    }
}
