package order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrder() {

        Order order = new Order(
                1,
                100,
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18050,
                100
        );

        assertEquals(1, order.getOrderId());
        assertEquals("AAPL", order.getSymbol());
        assertEquals(OrderSide.BUY, order.getSide());
        assertEquals(100, order.getRemainingQuantity());
        assertEquals(OrderStatus.NEW, order.getStatus());
    }

    @Test
    void shouldFillOrderPartially() {

        Order order = new Order(
                1,
                100,
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18050,
                100
        );

        order.accept();
        order.fill(40);

        assertEquals(60, order.getRemainingQuantity());
        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                order.getStatus()
        );
    }

    @Test
    void shouldFillOrderCompletely() {

        Order order = new Order(
                1,
                100,
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18050,
                100
        );

        order.accept();
        order.fill(100);

        assertEquals(0, order.getRemainingQuantity());
        assertEquals(
                OrderStatus.FILLED,
                order.getStatus()
        );
    }
}