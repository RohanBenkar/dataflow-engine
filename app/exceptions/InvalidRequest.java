package exceptions;

/**
 * Created by rbenkar on 8/29/16.
 */
public class InvalidRequest extends Exception {

    public InvalidRequest() {
        super("Invalid request");
    }

    public InvalidRequest(String message) {
        super(message);
    }

    public InvalidRequest(String message, Throwable cause) {
        super(message, cause);
    }
}
