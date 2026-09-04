package concurrency;

import account.TraderAccount;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class AtomicCashReservationServiceTest {

    @Test
    void shouldPreventConcurrentOverReservation()
            throws Exception {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        AccountLockManager lockManager =
                new AccountLockManager();

        AtomicCashReservationService service =
                new AtomicCashReservationService(
                        lockManager
                );

        int threadCount = 100;

        long reservationAmount =
                100_000;

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        threadCount
                );

        CountDownLatch start =
                new CountDownLatch(1);

        CountDownLatch done =
                new CountDownLatch(threadCount);

        List<Future<Boolean>> results =
                new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {

            results.add(
                    executor.submit(() -> {

                        try {

                            start.await();

                            service.reserve(
                                    account,
                                    reservationAmount
                            );

                            return true;

                        } catch (Exception e) {

                            return false;

                        } finally {

                            done.countDown();
                        }
                    })
            );
        }

        start.countDown();

        assertTrue(
                done.await(
                        5,
                        TimeUnit.SECONDS
                )
        );

        executor.shutdown();

        long successfulReservations =
                results.stream()
                        .mapToLong(
                                future -> {
                                    try {
                                        return future.get()
                                                ? 1
                                                : 0;
                                    } catch (Exception e) {
                                        return 0;
                                    }
                                }
                        )
                        .sum();

        /*
         * Only 10 × ₹100,000 can fit inside
         * ₹1,000,000.
         */
        assertEquals(
                10,
                successfulReservations
        );

        assertEquals(
                1_000_000,
                account.getReservedCash()
        );

        assertEquals(
                0,
                account.getAvailableCash()
        );

        assertTrue(
                account.getReservedCash()
                        <= account.getCashBalance()
        );
    }
}