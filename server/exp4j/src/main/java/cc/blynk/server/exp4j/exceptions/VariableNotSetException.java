package cc.blynk.server.exp4j.exceptions;

public class VariableNotSetException extends RuntimeException {

    public VariableNotSetException(String variableName) {
        super("The variable '" + variableName + "' has not been set.");
    }
}
