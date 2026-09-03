package book;

import order.Order;

import java.util.ArrayDeque;
import java.util.Deque;

public final class PriceLevel {

    private final long price;
    private final Deque<Order> orders;

    public PriceLevel(long price) {
        this.price = price;
        this.orders = new ArrayDeque<>();
    }

    public long getPrice() {
        return price;
    }

    public void addOrder(Order order) {
        orders.addLast(order);
    }

    public Order getFirstOrder() {
        return orders.peekFirst();
    }

    public Order removeFirstOrder() {
        return orders.pollFirst();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public int size() {
        return orders.size();
    }

    public boolean removeOrder(Order order) {
        return orders.remove(order);
    }
}