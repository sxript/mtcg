package db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class DBRetryHandlerImpl implements RetryHandler<SQLException> {
    public static final int DEFAULT_RETRIES = 5;
    public static final long DEFAULT_TIME_TO_WAIT_MS = 1000;

    private int retryCount;
    private long timeToWaitMS;

    public DBRetryHandlerImpl(int retryCount, long timeToWaitMS) {
        setRetryCount(retryCount);
        setTimeToWaitMS(timeToWaitMS);
    }

    public DBRetryHandlerImpl() {
        this(DEFAULT_RETRIES, DEFAULT_TIME_TO_WAIT_MS);
    }

    @Override
    public boolean isRetryable() {
        return (retryCount >= 0);
    }

    @Override
    public void waitForNextTry() {
        try {
            Thread.sleep(timeToWaitMS);
        }
        catch (InterruptedException iex) {
            iex.printStackTrace();
        }
    }

    @Override
    public void exceptionOccurred(Throwable e, String message) throws SQLException {
        retryCount--;
        if(!isRetryable()) {
            throw new SQLException("Retry limit exceeded; " + message, e);
        }
        waitForNextTry();
    }
}
