package se.quedro.challenge.exception;

public class ChallengeException extends RuntimeException {

    private final String reason;

    public ChallengeException(final String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
