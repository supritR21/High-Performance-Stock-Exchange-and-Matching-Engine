package concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class AccountLockManager {

    private final ConcurrentHashMap<
            Long,
            ReentrantLock
            > locks;

    public AccountLockManager() {

        this.locks =
                new ConcurrentHashMap<>();
    }

    public ReentrantLock getLock(
            long traderId
    ) {

        return locks.computeIfAbsent(
                traderId,
                id -> new ReentrantLock()
        );
    }

    public void removeLock(
            long traderId
    ) {

        locks.remove(traderId);
    }
}