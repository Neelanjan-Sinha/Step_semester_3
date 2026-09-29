public class Main {

    // Checks Java access rules
    static String classifyAccess(String fieldModifier, String accessorContext) {

        if (fieldModifier.equals("private")) {
            if (accessorContext.equals("SAME_CLASS"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        if (fieldModifier.equals("default")) {
            if (accessorContext.equals("SAME_CLASS") ||
                accessorContext.equals("SAME_PACKAGE"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        if (fieldModifier.equals("protected")) {
            if (accessorContext.equals("SAME_CLASS") ||
                accessorContext.equals("SAME_PACKAGE"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        if (fieldModifier.equals("public")) {
            return "ALLOWED";
        }

        return "DENIED";
    }

    // Summarizes attempts modifier-wise
    static String summarizeByModifier(String[][] attempts) {

        int privateAllowed = 0, privateDenied = 0;
        int defaultAllowed = 0, defaultDenied = 0;
        int protectedAllowed = 0, protectedDenied = 0;
        int publicAllowed = 0, publicDenied = 0;

        for (String[] attempt : attempts) {

            String modifier = attempt[0];
            String context = attempt[1];

            String result = classifyAccess(modifier, context);

            if (modifier.equals("private")) {
                if (result.equals("ALLOWED"))
                    privateAllowed++;
                else
                    privateDenied++;
            }

            else if (modifier.equals("default")) {
                if (result.equals("ALLOWED"))
                    defaultAllowed++;
                else
                    defaultDenied++;
            }

            else if (modifier.equals("protected")) {
                if (result.equals("ALLOWED"))
                    protectedAllowed++;
                else
                    protectedDenied++;
            }

            else if (modifier.equals("public")) {
                if (result.equals("ALLOWED"))
                    publicAllowed++;
                else
                    publicDenied++;
            }
        }

        return "private: " + privateAllowed + " allowed / " + privateDenied +
               " denied | default: " + defaultAllowed + " allowed / " + defaultDenied +
               " denied | protected: " + protectedAllowed + " allowed / " + protectedDenied +
               " denied | public: " + publicAllowed + " allowed / " + publicDenied +
               " denied";
    }


    // LibraryMember class
    static class LibraryMember {

        private String memberId;
        String branchCode;              // default/package-private
        protected int memberSince;
        public String displayName;

        // No-argument constructor is intentionally NOT provided

        LibraryMember(String memberId, String branchCode,
                      int memberSince, String displayName) {

            // Trim first
            String id = (memberId == null) ? "" : memberId.trim();

            // Blank or length less than 4
            if (id.length() < 4) {
                throw new IllegalArgumentException("Invalid membershipId");
            }

            this.memberId = id;
            this.branchCode = branchCode;
            this.memberSince = memberSince;
            this.displayName = displayName;
        }
    }


    public static void main(String[] args) {

        // Test classifyAccess()
        System.out.println(
            classifyAccess("private", "SAME_CLASS")
        );

        System.out.println(
            classifyAccess("protected", "DIFFERENT_PACKAGE")
        );


        // Test summarizeByModifier()
        String[][] attempts = {
            {"private", "SAME_CLASS"},
            {"private", "SAME_PACKAGE"},
            {"default", "SAME_PACKAGE"},
            {"default", "DIFFERENT_PACKAGE"},
            {"protected", "SAME_PACKAGE"},
            {"protected", "SAME_CLASS"},
            {"public", "DIFFERENT_PACKAGE"}
        };

        System.out.println(summarizeByModifier(attempts));


        // Test constructor
        try {
            LibraryMember member =
                new LibraryMember("LB89", "BR1", 0, "Priya Nair");

            System.out.println("Construction successful");

        } catch (IllegalArgumentException e) {
            System.out.println("construction rejected");
        }


        // Invalid ID: only 3 characters
        try {
            LibraryMember member =
                new LibraryMember("L89", "BR1", 0, "Priya Nair");

            System.out.println("Construction successful");

        } catch (IllegalArgumentException e) {
            System.out.println("construction rejected");
        }
    }
}
