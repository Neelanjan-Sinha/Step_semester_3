public class Main {

    // Checks access based on Java visibility rules
    static String classifyAccess(String fieldModifier, String accessorContext) {

        // private: only inside same class
        if (fieldModifier.equals("private")) {
            if (accessorContext.equals("SAME_CLASS"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        // default: same class and same package
        if (fieldModifier.equals("default")) {
            if (accessorContext.equals("SAME_CLASS") ||
                accessorContext.equals("SAME_PACKAGE"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        // protected
        if (fieldModifier.equals("protected")) {

            if (accessorContext.equals("SAME_CLASS") ||
                accessorContext.equals("SAME_PACKAGE") ||
                accessorContext.equals("SUBCLASS_DIFFERENT_PACKAGE_OWN_TYPE"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        // public: accessible everywhere
        if (fieldModifier.equals("public")) {
            return "ALLOWED";
        }

        return "DENIED";
    }


    // Converts underscore-separated context to title case
    static String describeContext(String accessorContext) {

        String[] words = accessorContext.split("_");

        String result = "";

        for (String word : words) {

            if (word.length() == 0)
                continue;

            // Convert to lowercase first
            word = word.toLowerCase();

            // Capitalize first character
            word = Character.toUpperCase(word.charAt(0))
                    + word.substring(1);

            if (result.length() > 0)
                result += " ";

            result += word;
        }

        return result;
    }


    public static void main(String[] args) {

        // Test 1
        System.out.println(
            classifyAccess(
                "protected",
                "SUBCLASS_DIFFERENT_PACKAGE_OWN_TYPE"
            )
        );

        // Test 2
        System.out.println(
            classifyAccess(
                "protected",
                "SUBCLASS_DIFFERENT_PACKAGE_PARENT_TYPE"
            )
        );

        // Test 3
        System.out.println(
            describeContext(
                "SUBCLASS_DIFFERENT_PACKAGE_OWN_TYPE"
            )
        );
    }
}
