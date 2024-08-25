package exceptions;

public class CollisionTaskException extends RuntimeException {

    public CollisionTaskException() {
        super();
    }

    public CollisionTaskException(String message) {
        super(message);
    }
}