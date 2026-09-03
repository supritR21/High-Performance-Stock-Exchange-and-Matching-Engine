package account;

import matching.Trade;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TradeSettlementServiceTest {

    @Test
    void shouldSettleTradeBetweenBuyerAndSeller() {

        TraderAccount buyer =
                new TraderAccount(
                        1,
                        2_000_000
                );

        TraderAccount seller =
                new TraderAccount(
                        2,
                        500_000
                );

        seller.getPortfolio()
                .addPosition(
                        "AAPL",
                        100
                );

        Map<Long, TraderAccount> accounts =
                new HashMap<>();

        accounts.put(
                buyer.getTraderId(),
                buyer
        );

        accounts.put(
                seller.getTraderId(),
                seller
        );

        Trade trade =
                new Trade(
                        1,
                        101,
                        102,
                        1,
                        2,
                        "AAPL",
                        18000,
                        100
                );

        TradeSettlementService settlement =
                new TradeSettlementService();

        settlement.settle(
                trade,
                accounts
        );

        /*
         * Buyer:
         *
         * 2,000,000
         * -1,800,000
         * = 200,000
         */
        assertEquals(
                200_000,
                buyer.getCashBalance()
        );

        assertEquals(
                100,
                buyer.getPortfolio()
                        .getQuantity("AAPL")
        );

        /*
         * Seller:
         *
         * 500,000
         * +1,800,000
         * = 2,300,000
         */
        assertEquals(
                2_300_000,
                seller.getCashBalance()
        );

        assertEquals(
                0,
                seller.getPortfolio()
                        .getQuantity("AAPL")
        );
    }

    @Test
    void shouldRejectSettlementWhenSellerLacksShares() {

        TraderAccount buyer =
                new TraderAccount(
                        1,
                        2_000_000
                );

        TraderAccount seller =
                new TraderAccount(
                        2,
                        500_000
                );

        Map<Long, TraderAccount> accounts =
                new HashMap<>();

        accounts.put(1L, buyer);
        accounts.put(2L, seller);

        Trade trade =
                new Trade(
                        1,
                        101,
                        102,
                        1,
                        2,
                        "AAPL",
                        18000,
                        100
                );

        TradeSettlementService settlement =
                new TradeSettlementService();

        assertThrows(
                IllegalStateException.class,
                () -> settlement.settle(
                        trade,
                        accounts
                )
        );

        /*
         * Buyer should remain unchanged.
         */
        assertEquals(
                2_000_000,
                buyer.getCashBalance()
        );
    }
}