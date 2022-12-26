package db;

public interface RetryHandler<E extends Throwable> {
    boolean isRetryable();
    void waitForNextTry();
    void exceptionOccurred(Throwable e, String message) throws E;
}
