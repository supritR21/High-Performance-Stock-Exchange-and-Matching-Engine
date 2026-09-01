package order;

import java.util.concurrent.atomic.AtomicLong;

public final class OrderIdGenerator {

    private final AtomicLong counter = new AtomicLong(0);

    public long nextId() {
        return counter.incrementAndGet();
    }
}