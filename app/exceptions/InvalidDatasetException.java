package exceptions;

/**
 * Created by rbenkar on 8/29/16.
 */
public class InvalidDatasetException extends Exception {

    public InvalidDatasetException() {
        super("Invalid dataset");
    }

    public InvalidDatasetException(String message) {
        super(message);
    }

    public InvalidDatasetException(String message, Throwable cause) {
        super(message, cause);
    }
}
