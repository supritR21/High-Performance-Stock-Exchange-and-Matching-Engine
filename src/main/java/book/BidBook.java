package book;

import order.Order;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class BidBook {

    private final NavigableMap<Long, PriceLevel> levels;

    public BidBook() {
        this.levels = new TreeMap<>(Collections.reverseOrder());
    }

    public void add(Order order) {

        PriceLevel level =
                levels.computeIfAbsent(
                        order.getPrice(),
                        PriceLevel::new
                );

        level.addOrder(order);
    }

    public PriceLevel getBestLevel() {
        return levels.isEmpty()
                ? null
                : levels.firstEntry().getValue();
    }

    public boolean isEmpty() {
        return levels.isEmpty();
    }

    public int numberOfPriceLevels() {
        return levels.size();
    }

    public void removePriceLevel(long price) {
        levels.remove(price);
    }
}