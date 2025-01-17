package cl.vampfern.tenpo_backend_challenge.exception;

import lombok.Builder;

@Builder
public class RateLimitException extends RuntimeException {

    public RateLimitException() {
        super("Rate limit exceeded");
    }

    public RateLimitException(String message) {
        super(message);
    }

}
