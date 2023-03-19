package nl.dylanvdbrink.oauthassignment.auth;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
