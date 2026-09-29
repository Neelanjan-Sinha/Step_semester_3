import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {

    static class LibraryMember {

        private String membershipId;
        private String name;
        private boolean premiumMember;

        // Write-once security answer
        private String securityAnswerHash;
        private boolean securityAnswerSet = false;


        // 1. No-argument constructor
        public LibraryMember() {
            this(null, null);
        }


        // 2. Name-only constructor
        public LibraryMember(String name) {
            this(null, name);
        }


        // 3. ID + Name constructor
        public LibraryMember(String membershipId, String name) {
            this.membershipId = membershipId;
            this.name = name;
            this.premiumMember = false;
        }


        // JavaBean getter
        public String getMembershipId() {
            return membershipId;
        }


        // JavaBean setter
        public void setMembershipId(String id) {
            this.membershipId = id;
        }


        // Getter for name
        public String getName() {
            return name;
        }


        // Setter for name
        public void setName(String name) {
            this.name = name;
        }


        // JavaBean getter for boolean
        public boolean isPremiumMember() {
            return premiumMember;
        }


        // JavaBean setter
        public void setPremiumMember(boolean premium) {
            this.premiumMember = premium;
        }


        // Write-only security answer
        public void setSecurityAnswer(String answer) {

            // Ignore second attempt
            if (securityAnswerSet) {
                return;
            }

            if (answer == null) {
                return;
            }

            securityAnswerHash = hashAnswer(answer);
            securityAnswerSet = true;
        }


        // One-way hashing
        private String hashAnswer(String answer) {

            try {
                MessageDigest md =
                    MessageDigest.getInstance("SHA-256");

                byte[] hash = md.digest(answer.getBytes());

                StringBuilder result = new StringBuilder();

                for (byte b : hash) {
                    result.append(String.format("%02x", b));
                }

                return result.toString();

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args) {

        // Example 1
        LibraryMember m1 =
            new LibraryMember("Priya Nair");

        System.out.println(m1.getMembershipId());


        // Example 2
        LibraryMember m2 =
            new LibraryMember("LIB-8841", "Priya Nair");

        System.out.println(m2.getMembershipId());


        // Example 3
        LibraryMember m3 =
            new LibraryMember();

        m3.setMembershipId("LIB-8841");
        m3.setMembershipId("FAKE-0000");

        System.out.println(m3.getMembershipId());


        // Security answer
        m3.setSecurityAnswer("mySecretAnswer");

        // Second attempt is ignored
        m3.setSecurityAnswer("newAnswer");
    }
}
