import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.Map;

public class Main {
    public static boolean pass = true;
    public static final Set<String> reserved = Set.of("TAGS", "BEGIN", "SEQUENCE", "INTEGER", "DATE", "END");
    public static final Map<Character, String> charTokens = Map.of(
            '\"', "QUOTATION MARK",
            '(', "LEFT PARENTHESIS",
            ')', "RIGHT PARENTHESIS",
            ',', "COMMA",
            '-', "HYPHEN-MINUS",
            ':', "COLON",
            '=', "EQUAL SIGN",
            '{', "LEFT CURLY BRACKET",
            '|', "VERTICAL LINE",
            '}', "RIGHT CURLY BRACKET"
    );


    public static void main(String[] args) {
        String file = "src/test.txt";
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                evaluateLine(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        if (pass) {
            System.out.println("\nSuccessfully identified all tokens");
        } else {
            System.out.println("\nCould not successfully identify all tokens");
        }

    }

    public static void evaluateLine (String line) {
        int i = 0;

        while (i < line.length()) {
            char c = line.charAt(i);

            if (c == ' ') {
                i++;
                continue;
            }

            if (assignmentOperator(line, i)) {
                System.out.printf("Assignment Operator: %14s%n", "::=");
                i += 3;
            } else if (c >= 'A' && c <= 'Z') {
                String typeReference = getTypeReference(line.substring(i));
                if (typeReference == null) {
                    System.out.println("Error: Invalid Type Reference on line \"" + line + "\"");
                    pass = false;
                    break;
                } else if (reserved.contains(typeReference)) {
                    System.out.printf("Reserved Word: %20s%n", typeReference);
                    i += typeReference.length();
                } else {
                    System.out.printf("TypeReference: %20s%n", typeReference);
                    i += typeReference.length();
                }
            } else if (c >= 'a' && c <= 'z') {
                String identifier = getIdentifier(line.substring(i));
                if (identifier == null) {
                    System.out.println("Error: Invalid Identifier on line \"" + line + "\"");
                    pass = false;
                    break;
                } else {
                    System.out.printf("Identifier: %23s%n", identifier);
                    i += identifier.length();
                }
            } else if (c >= '0' && c <= '9') {
                String number = getNumber(line.substring(i));
                if (number == null) {
                    System.out.println("Error: Invalid number on line \"" + line + "\"");
                    pass = false;
                    break;
                } else {
                    System.out.printf("Number: %27s%n", number);
                    i += number.length();
                }
            } else if (rangeSeparator(line, i)) {
                System.out.printf("Range Separator: %18s%n", "..");
                i += 2;
            } else if (charTokens.containsKey(line.charAt(i))) {
                System.out.printf("Token: %28s%n", charTokens.get(line.charAt(i)));
                i++;
            } else {
                System.out.println("Error: Invalid token on line \"" + line + "\"");
                pass = false;
                break;
            }
        }
    }

    public static boolean assignmentOperator(String line, int i) {
        return line.startsWith("::=", i);
    }

    public static String getTypeReference(String line) {
        StringBuilder ans = new StringBuilder();
        boolean lastWasHyphen = false;

        for (char c : line.toCharArray()) {
            if (lastWasHyphen) {
                if (c == '-' || c == ' ' || c == '\0') {
                    return null;
                } else {
                    lastWasHyphen = false;
                }
            }

            if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) || (c >= '0' && c <= '9') || c == '-') {
                ans.append(c);
                if (c == '-') {
                    lastWasHyphen = true;
                }
            } else if (c == ' ' || charTokens.containsKey(c)) {
                return ans.toString();
            } else {
                return null;
            }
        }

        return ans.toString();
    }

    public static String getIdentifier(String line) {
        StringBuilder ans = new StringBuilder();
        boolean lastWasHyphen = false;

        for (char c : line.toCharArray()) {
            if (lastWasHyphen) {
                if (c == '-' || c == ' ' || c == '\0') {
                    return null;
                } else {
                    lastWasHyphen = false;
                }
            }

            if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) || (c >= '0' && c <= '9') || c == '-') {
                ans.append(c);
                if (c == '-') {
                    lastWasHyphen = true;
                }
            } else if (c == ' ' || charTokens.containsKey(c)) {
                return ans.toString();
            } else {
                return null;
            }
        }

        return ans.toString();
    }

    public static String getNumber(String line) {
        StringBuilder ans = new StringBuilder();
        if (line.charAt(0) == '0' && line.length() > 1 && (line.charAt(1) >= '0' && line.charAt(1) <= '9')) {
            return null;
        }

        for (char c : line.toCharArray()) {
            if ((c >= '0' && c <= '9')) {
                ans.append(c);
            } else if (c == ' ' || charTokens.containsKey(c)) {
                return ans.toString();
            } else {
                return null;
            }
        }

        return ans.toString();
    }

    public static boolean rangeSeparator(String line, int i) {
        return line.startsWith("..", i);
    }
}