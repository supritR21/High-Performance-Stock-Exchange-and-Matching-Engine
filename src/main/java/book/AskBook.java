package book;

import order.Order;

import java.util.NavigableMap;
import java.util.TreeMap;

public final class AskBook {

    private final NavigableMap<Long, PriceLevel> levels;

    public AskBook() {
        this.levels = new TreeMap<>();
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

    public PriceLevel getPriceLevel(long price) {
        return levels.get(price);
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