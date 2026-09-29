public class Main {

    static class PatientProfile {

        private String patientId;
        private String name;
        private boolean discharged;

        // Write-once PIN storage
        private String lockerPinHash;
        private boolean pinSet = false;


        // 1. No-argument constructor
        public PatientProfile() {
            this(null, null);
        }


        // 2. Name-only constructor
        public PatientProfile(String name) {
            this(null, name);
        }


        // 3. ID + Name constructor
        public PatientProfile(String patientId, String name) {
            this.patientId = patientId;
            this.name = name;
            this.discharged = false;
        }


        // JavaBean getter
        public String getPatientId() {
            return patientId;
        }


        // JavaBean setter
        public void setPatientId(String id) {

            // Write-once property
            if (patientId == null) {
                patientId = id;
            }
        }


        // Name getter
        public String getName() {
            return name;
        }


        // Name setter
        public void setName(String name) {
            this.name = name;
        }


        // Boolean JavaBean getter
        public boolean isDischarged() {
            return discharged;
        }


        // Boolean setter
        public void setDischarged(boolean discharged) {
            this.discharged = discharged;
        }


        // Write-only locker PIN
        public void setLockerPin(String pin) {

            // Ignore later attempts
            if (pinSet) {
                return;
            }

            // PIN must be 4-6 digits
            if (pin == null || !pin.matches("\\d{4,6}")) {
                return;
            }

            // Store a deterministic one-way transformation
            lockerPinHash = hashPin(pin);

            pinSet = true;
        }


        // Simple deterministic one-way transformation
        private String hashPin(String pin) {

            int hash = 0;

            for (int i = 0; i < pin.length(); i++) {
                hash = hash * 31 + pin.charAt(i);
            }

            return Integer.toHexString(hash);
        }
    }


    public static void main(String[] args) {

        // Example 1
        PatientProfile p1 =
                new PatientProfile("Arjun Iyer");

        System.out.println(p1.getPatientId());


        // Example 2
        PatientProfile p2 =
                new PatientProfile("MT2026-0142", "Arjun Iyer");

        System.out.println(p2.getPatientId());


        // Example 3
        PatientProfile p3 =
                new PatientProfile();

        p3.setPatientId("MT2026-0142");
        p3.setPatientId("HACKED-0000");

        System.out.println(p3.getPatientId());


        // Locker PIN
        p3.setLockerPin("1234");

        // Second attempt is ignored
        p3.setLockerPin("999999");
    }
}
