// Save as Main.java
interface Exportable {
    String exportData();
}

// Single shared home for the counter, since the two exporters share no parent class
class ExportStats {
    private static int totalExports = 0;
    private ExportStats() {}
    static void record() { totalExports++; }
    static int get() { return totalExports; }
}

class ReportGenerator implements Exportable {
    private final String reportName;

    public ReportGenerator(String reportName) {
        if (reportName == null || reportName.isBlank()) throw new IllegalArgumentException("reportName is blank");
        this.reportName = reportName;
    }

    @Override
    public String exportData() {
        ExportStats.record();
        return "Exported report: " + reportName;
    }
}

class UserProfile implements Exportable {
    private final String username;

    public UserProfile(String username) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username is blank");
        this.username = username;
    }

    @Override
    public String exportData() {
        ExportStats.record();
        return "Exported profile: " + username;
    }
}

public class Main {
    static int getTotalExports() { return ExportStats.get(); }

    static void exportAll(Exportable[] items) {
        for (Exportable item : items) {
            System.out.println(item.exportData());
        }
    }

    public static void main(String[] args) {
        ReportGenerator r = new ReportGenerator("Sales Q1");
        UserProfile u = new UserProfile("jane_doe");

        Exportable ref = r;                                   // upcast to the interface type
        exportAll(new Exportable[]{ref, u});                  // both messages printed
        System.out.println(getTotalExports());                // 2

        // Direct calls also work (and would add to the counter)
        System.out.println(r.exportData());                   // Exported report: Sales Q1
        System.out.println(u.exportData());                   // Exported profile: jane_doe
    }
}
