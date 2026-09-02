package book;

import exchange.Instrument;
import order.Order;
import order.OrderSide;
import order.OrderType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    @Test
    void shouldAddBuyOrder() {

        Instrument apple =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(apple);

        Order order =
                new Order(
                        1,
                        100,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        book.addOrder(order);

        assertNotNull(book.getBidBook().getBestLevel());

        assertEquals(
                18000,
                book.getBidBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldAddSellOrder() {

        Instrument apple =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(apple);

        Order order =
                new Order(
                        1,
                        100,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18100,
                        100
                );

        book.addOrder(order);

        assertNotNull(book.getAskBook().getBestLevel());

        assertEquals(
                18100,
                book.getAskBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldRespectPricePriority() {

        Instrument apple =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(apple);

        book.addOrder(new Order(
                1, 100, "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18000, 100
        ));

        book.addOrder(new Order(
                2, 101, "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18100, 100
        ));

        book.addOrder(new Order(
                3, 102, "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                17900, 100
        ));

        assertEquals(
                18100,
                book.getBidBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldRespectTimePriorityAtSamePrice() {

        Instrument apple =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(apple);

        Order first = new Order(
                1, 100, "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18000, 100
        );

        Order second = new Order(
                2, 101, "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                18000, 200
        );

        book.addOrder(first);
        book.addOrder(second);

        PriceLevel level =
                book.getBidBook().getBestLevel();

        assertEquals(
                1,
                level.getFirstOrder().getOrderId()
        );
    }
}