import java.util.Arrays;

public class Main {

    // DischargeSummary
    static class DischargeSummary {

        // Immutable fields
        private final String patientId;
        private final String[] medicationCodes;

        // Constructor
        public DischargeSummary(String patientId, String[] medicationCodes) {

            if (medicationCodes == null) {
                throw new IllegalArgumentException("Invalid medication codes");
            }

            // Validate every medication code
            for (String code : medicationCodes) {

                if (code == null ||
                    !code.matches("MED-[A-Z]\\d{2}")) {

                    throw new IllegalArgumentException(
                            "Invalid medication code"
                    );
                }
            }

            this.patientId = patientId;

            // Defensive copy
            this.medicationCodes =
                    Arrays.copyOf(medicationCodes, medicationCodes.length);
        }


        // Defensive copy when returning
        public String[] getMedicationCodes() {
            return Arrays.copyOf(
                    medicationCodes,
                    medicationCodes.length
            );
        }


        // Creates a new object with one corrected code
        public DischargeSummary withCorrectedMedication(
                int index,
                String newCode) {

            if (index < 0 || index >= medicationCodes.length) {
                throw new IllegalArgumentException("Invalid index");
            }

            if (newCode == null ||
                !newCode.matches("MED-[A-Z]\\d{2}")) {

                throw new IllegalArgumentException(
                        "Invalid medication code"
                );
            }

            // Make a copy
            String[] newCodes =
                    Arrays.copyOf(
                            medicationCodes,
                            medicationCodes.length
                    );

            // Modify only the new array
            newCodes[index] = newCode;

            // Return a new object
            return new DischargeSummary(
                    patientId,
                    newCodes
            );
        }


        public String getPatientId() {
            return patientId;
        }
    }


    // Critical-care variant
    static class CriticalCareDischargeSummary
            extends DischargeSummary {

        public CriticalCareDischargeSummary(
                String patientId,
                String[] medicationCodes) {

            super(patientId, medicationCodes);
        }
    }


    // Static initialization
    static {
        System.out.println("Nightly discharge ledger ready");
    }


    // Process nightly batch
    public static String processNightlyBatch(
            DischargeSummary[] summaries) {

        int processed = 0;
        int nullSkipped = 0;
        int criticalCare = 0;
        int routine = 0;

        if (summaries == null) {
            return "0 processed | 0 null skipped | 0 critical-care | 0 routine";
        }

        for (DischargeSummary summary : summaries) {

            // Skip null entries
            if (summary == null) {
                nullSkipped++;
                continue;
            }

            processed++;

            // Check actual runtime type
            if (summary instanceof CriticalCareDischargeSummary) {
                criticalCare++;
            } else {
                routine++;
            }
        }

        return processed + " processed | "
                + nullSkipped + " null skipped | "
                + criticalCare + " critical-care | "
                + routine + " routine";
    }


    public static void main(String[] args) {

        // Example 1
        try {

            DischargeSummary d1 =
                    new DischargeSummary(
                            "MT2026-0142",
                            new String[]{"MED-A", "bad"}
                    );

            System.out.println("construction successful");

        } catch (IllegalArgumentException e) {

            System.out.println("construction rejected");
        }


        // Example 2
        DischargeSummary d =
                new DischargeSummary(
                        "MT2026-0142",
                        new String[]{"MED-A", "MED-B"}
                );

        // Correct one medication
        d = d.withCorrectedMedication(0, "MED-C");

        System.out.println(d.getMedicationCodes()[0]);


        // Defensive-copy test
        String[] codes = d.getMedicationCodes();

        codes[0] = "HACKED";

        System.out.println(d.getMedicationCodes()[0]);


        // Example 3
        DischargeSummary[] summaries = {

                new CriticalCareDischargeSummary(
                        "MT001",
                        new String[]{"MED-X"}
                ),

                null,

                new DischargeSummary(
                        "MT002",
                        new String[]{"MED-Y"}
                )
        };

        System.out.println(
                processNightlyBatch(summaries)
        );
    }
}
