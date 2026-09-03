package matching;

import book.OrderBook;
import book.PriceLevel;
import exchange.Instrument;
import order.Order;
import order.OrderIdGenerator;
import order.OrderSide;
import order.OrderStatus;
import order.OrderType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingEngineTest {

    @Test
    void shouldMatchBuyAndSellOrder() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sellOrder =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        book.addOrder(sellOrder);

        Order buyOrder =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(book, buyOrder);

        assertEquals(
                1,
                result.getTrades().size()
        );

        assertEquals(
                100,
                result.getTrades()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                OrderStatus.FILLED,
                buyOrder.getStatus()
        );

        assertEquals(
                OrderStatus.FILLED,
                sellOrder.getStatus()
        );
    }

    @Test
    void shouldPartiallyFillOrder() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sellOrder =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        40
                );

        book.addOrder(sellOrder);

        Order buyOrder =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(book, buyOrder);

        assertEquals(
                40,
                result.getTrades()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                60,
                buyOrder.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                buyOrder.getStatus()
        );

        assertFalse(
                book.getBidBook().isEmpty()
        );

        assertEquals(
                18000,
                book.getBidBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldMatchAcrossMultiplePriceLevels() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sell1 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        40
                );

        Order sell2 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18100,
                        30
                );

        Order sell3 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18200,
                        50
                );

        book.addOrder(sell1);
        book.addOrder(sell2);
        book.addOrder(sell3);

        Order buy =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18100,
                        100
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(book, buy);

        assertEquals(
                2,
                result.getTrades().size()
        );

        assertEquals(
                40,
                result.getTrades()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                18000,
                result.getTrades()
                        .get(0)
                        .getPrice()
        );

        assertEquals(
                30,
                result.getTrades()
                        .get(1)
                        .getQuantity()
        );

        assertEquals(
                18100,
                result.getTrades()
                        .get(1)
                        .getPrice()
        );

        assertEquals(
                30,
                buy.getRemainingQuantity()
        );

        /*
         * 18200 ask must remain untouched.
         */
        assertEquals(
                18200,
                book.getAskBook()
                        .getBestLevel()
                        .getPrice()
        );

        /*
         * Remaining BUY becomes resting liquidity.
         */
        assertEquals(
                18100,
                book.getBidBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldRestUnmatchedSellOrder() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sell =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18500,
                        100
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(book, sell);

        assertTrue(
                result.getTrades().isEmpty()
        );

        assertEquals(
                100,
                sell.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.ACCEPTED,
                sell.getStatus()
        );

        assertEquals(
                18500,
                book.getAskBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldExecuteMarketBuyAcrossMultipleLevels() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sell1 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        40
                );

        Order sell2 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18100,
                        30
                );

        book.addOrder(sell1);
        book.addOrder(sell2);

        Order marketBuy =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.MARKET,
                        0,
                        60
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(
                        book,
                        marketBuy
                );

        assertEquals(
                2,
                result.getTrades().size()
        );

        assertEquals(
                18000,
                result.getTrades()
                        .get(0)
                        .getPrice()
        );

        assertEquals(
                40,
                result.getTrades()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                18100,
                result.getTrades()
                        .get(1)
                        .getPrice()
        );

        assertEquals(
                20,
                result.getTrades()
                        .get(1)
                        .getQuantity()
        );

        assertEquals(
                0,
                marketBuy.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.FILLED,
                marketBuy.getStatus()
        );

        /*
         * Remaining ASK:
         * 18100 -> 10
         */
        assertEquals(
                18100,
                book.getAskBook()
                        .getBestLevel()
                        .getPrice()
        );
    }

    @Test
    void shouldPartiallyFillMarketOrderWhenLiquidityRunsOut() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sell =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        40
                );

        book.addOrder(sell);

        Order marketBuy =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.MARKET,
                        0,
                        100
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(
                        book,
                        marketBuy
                );

        assertEquals(
                1,
                result.getTrades().size()
        );

        assertEquals(
                40,
                result.getTrades()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                60,
                marketBuy.getRemainingQuantity()
        );

        assertEquals(
                OrderStatus.PARTIALLY_FILLED,
                marketBuy.getStatus()
        );

        /*
         * Market order does NOT rest.
         */
        assertTrue(
                book.getBidBook().isEmpty()
        );
    }

    @Test
    void shouldExecuteMarketSellAgainstBestBids() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order buy1 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18100,
                        30
                );

        Order buy2 =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        40
                );

        book.addOrder(buy1);
        book.addOrder(buy2);

        Order marketSell =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.MARKET,
                        0,
                        50
                );

        MatchingEngine engine =
                new MatchingEngine();

        MatchResult result =
                engine.process(
                        book,
                        marketSell
                );

        assertEquals(
                2,
                result.getTrades().size()
        );

        assertEquals(
                18100,
                result.getTrades()
                        .get(0)
                        .getPrice()
        );

        assertEquals(
                30,
                result.getTrades()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                18000,
                result.getTrades()
                        .get(1)
                        .getPrice()
        );

        assertEquals(
                20,
                result.getTrades()
                        .get(1)
                        .getQuantity()
        );

        assertEquals(
                0,
                marketSell.getRemainingQuantity()
        );
    }

    @Test
    void shouldCancelRestingBuyOrder() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order buy =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        book.addOrder(buy);

        assertFalse(
                book.getBidBook().isEmpty()
        );

        boolean cancelled =
                book.cancelOrder(buy);

        assertTrue(cancelled);

        assertEquals(
                OrderStatus.CANCELLED,
                buy.getStatus()
        );

        assertTrue(
                book.getBidBook().isEmpty()
        );
    }

    @Test
    void shouldCancelRestingSellOrder() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sell =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        book.addOrder(sell);

        boolean cancelled =
                book.cancelOrder(sell);

        assertTrue(cancelled);

        assertEquals(
                OrderStatus.CANCELLED,
                sell.getStatus()
        );

        assertTrue(
                book.getAskBook().isEmpty()
        );
    }

    @Test
    void shouldNotCancelFilledOrder() {

        Instrument instrument =
                new Instrument("AAPL", "Apple Inc.");

        OrderBook book =
                new OrderBook(instrument);

        OrderIdGenerator ids =
                new OrderIdGenerator();

        Order sell =
                new Order(
                        ids.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        book.addOrder(sell);

        Order buy =
                new Order(
                        ids.nextId(),
                        2,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        MatchingEngine engine =
                new MatchingEngine();

        engine.process(book, buy);

        assertEquals(
                OrderStatus.FILLED,
                sell.getStatus()
        );

        assertThrows(
                IllegalStateException.class,
                () -> book.cancelOrder(sell)
        );
    }
}