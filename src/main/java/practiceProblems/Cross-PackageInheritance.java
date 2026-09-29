public class Main {

    // Handles all 5 accessor contexts
    static String classifyAccess(String fieldModifier, String accessorContext) {

        // private
        if (fieldModifier.equals("private")) {
            if (accessorContext.equals("SAME_CLASS"))
                return "ALLOWED";
            else
                return "DENIED";
        }

        // default / package-private
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

        // public
        if (fieldModifier.equals("public")) {
            return "ALLOWED";
        }

        return "DENIED";
    }


    // Converts underscore-separated text into title case
    static String describeContext(String accessorContext) {

        String[] words = accessorContext.split("_");

        String result = "";

        for (String word : words) {

            if (word.length() == 0)
                continue;

            word = word.toLowerCase();

            word = Character.toUpperCase(word.charAt(0))
                    + word.substring(1);

            if (!result.isEmpty())
                result += " ";

            result += word;
        }

        return result;
    }


    public static void main(String[] args) {

        // Example 1
        System.out.println(
            classifyAccess(
                "protected",
                "SUBCLASS_DIFFERENT_PACKAGE_OWN_TYPE"
            )
        );

        // Example 2
        System.out.println(
            classifyAccess(
                "protected",
                "SUBCLASS_DIFFERENT_PACKAGE_PARENT_TYPE"
            )
        );

        // Example 3
        System.out.println(
            describeContext(
                "SUBCLASS_DIFFERENT_PACKAGE_PARENT_TYPE"
            )
        );
    }
}
