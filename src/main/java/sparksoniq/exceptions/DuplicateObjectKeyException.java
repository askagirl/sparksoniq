package sparksoniq.exceptions;

public class DuplicateObjectKeyException extends SparksoniqRuntimeException{
    public DuplicateObjectKeyException(String message) {
        super(message, ErrorCodes.DuplicatePairNameErrorCode);
    }
}