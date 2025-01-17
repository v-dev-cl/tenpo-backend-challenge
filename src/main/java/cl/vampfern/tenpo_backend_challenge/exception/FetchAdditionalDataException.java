package cl.vampfern.tenpo_backend_challenge.exception;

import lombok.Builder;

@Builder
public class FetchAdditionalDataException extends RuntimeException {
    public FetchAdditionalDataException() {
        super("Could not fetch additional data");
    }

    public FetchAdditionalDataException(String message) {
        super(message);
    }
}
