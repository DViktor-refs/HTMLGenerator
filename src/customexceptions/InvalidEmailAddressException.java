package customexceptions;

public class InvalidEmailAddressException extends RuntimeException{

    public InvalidEmailAddressException(String message) {
        super(message);
    }

}
