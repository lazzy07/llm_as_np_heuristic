package lazzy07.llmheuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SabreFunctionParser {
	public static SabreFunction parseFunction(String functionStr) {
        // Regex to match the function name and arguments
        String regex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(functionStr);

        if (matcher.matches()) {
            String methodName = matcher.group(1);
            String argumentsStr = matcher.group(2).trim();

            List<String> arguments = new ArrayList<>();
            if (!argumentsStr.isEmpty()) {
                String[] argsArray = argumentsStr.split("\\s*,\\s*");
                for (String arg : argsArray) {
                    arguments.add(arg);
                }
            }

            return new SabreFunction(methodName, arguments);
        }

        return null;
    }
}
