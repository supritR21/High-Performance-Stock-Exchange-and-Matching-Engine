package concurrency;

import account.TraderAccount;

import java.util.concurrent.locks.ReentrantLock;

public final class AtomicCashReservationService {

    private final AccountLockManager lockManager;

    public AtomicCashReservationService(
            AccountLockManager lockManager
    ) {

        this.lockManager = lockManager;
    }

    public void reserve(
            TraderAccount account,
            long amount
    ) {

        ReentrantLock lock =
                lockManager.getLock(
                        account.getTraderId()
                );

        lock.lock();

        try {

            /*
             * The check and modification happen
             * inside the SAME critical section.
             */
            if (amount > account.getAvailableCash()) {

                throw new IllegalStateException(
                        "Insufficient available cash"
                );
            }

            account.reserveCash(amount);

        } finally {

            lock.unlock();
        }
    }

    public void release(
            TraderAccount account,
            long amount
    ) {

        ReentrantLock lock =
                lockManager.getLock(
                        account.getTraderId()
                );

        lock.lock();

        try {

            account.releaseCash(amount);

        } finally {

            lock.unlock();
        }
    }
}