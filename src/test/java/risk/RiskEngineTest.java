package risk;

import account.TraderAccount;
import order.Order;
import order.OrderIdGenerator;
import order.OrderSide;
import order.OrderStatus;
import order.OrderType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RiskEngineTest {

    private final OrderIdGenerator idGenerator =
            new OrderIdGenerator();

    @Test
    void shouldApproveBuyWhenEnoughCash() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertTrue(
                result.isApproved()
        );
    }

    @Test
    void shouldRejectBuyWhenInsufficientCash() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertFalse(
                result.isApproved()
        );

        assertTrue(
                result.getReason()
                        .contains("Insufficient cash")
        );
    }

    @Test
    void shouldApproveSellWhenEnoughShares() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        account.getPortfolio()
                .addPosition(
                        "AAPL",
                        100
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        50
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertTrue(
                result.isApproved()
        );
    }

    @Test
    void shouldRejectSellWhenInsufficientShares() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        account.getPortfolio()
                .addPosition(
                        "AAPL",
                        50
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.SELL,
                        OrderType.LIMIT,
                        18000,
                        100
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertFalse(
                result.isApproved()
        );

        assertTrue(
                result.getReason()
                        .contains("Insufficient position")
        );
    }

    @Test
    void shouldRejectOrderForWrongTrader() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        999,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        10
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertFalse(
                result.isApproved()
        );

        assertEquals(
                "Order trader does not match account",
                result.getReason()
        );
    }

    @Test
    void shouldRejectCancelledOrder() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        18000,
                        10
                );

        order.cancel();

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertFalse(
                result.isApproved()
        );

        assertEquals(
                "Cancelled order cannot be submitted",
                result.getReason()
        );
    }

    @Test
    void shouldRejectMarketBuyWithoutRiskPrice() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.MARKET,
                        0,
                        10
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertFalse(
                result.isApproved()
        );

        assertEquals(
                "Market BUY requires a maximum risk price",
                result.getReason()
        );
    }

    @Test
    void shouldRejectOverflowingOrderValue() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        Long.MAX_VALUE
                );

        Order order =
                new Order(
                        idGenerator.nextId(),
                        1,
                        "AAPL",
                        OrderSide.BUY,
                        OrderType.LIMIT,
                        Long.MAX_VALUE,
                        2
                );

        RiskEngine riskEngine =
                new RiskEngine();

        RiskCheckResult result =
                riskEngine.check(
                        account,
                        order
                );

        assertFalse(
                result.isApproved()
        );

        assertEquals(
                "Order value exceeds supported range",
                result.getReason()
        );
    }
}