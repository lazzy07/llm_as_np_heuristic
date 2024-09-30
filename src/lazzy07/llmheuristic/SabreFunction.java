package lazzy07.llmheuristic;

import java.util.List;

public class SabreFunction {
	private final String methodName;
    private final List<String> arguments;

    public SabreFunction(String methodName, List<String> arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
