package account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TraderAccountTest {

    @Test
    void shouldCreateAccount() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        assertEquals(
                1,
                account.getTraderId()
        );

        assertEquals(
                1_000_000,
                account.getCashBalance()
        );

        assertEquals(
                0,
                account.getPortfolio()
                        .getQuantity("AAPL")
        );
    }

    @Test
    void shouldDepositAndWithdrawCash() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        account.deposit(500_000);

        assertEquals(
                1_500_000,
                account.getCashBalance()
        );

        account.withdraw(200_000);

        assertEquals(
                1_300_000,
                account.getCashBalance()
        );
    }

    @Test
    void shouldBuyShares() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        account.buy(
                "AAPL",
                100,
                18000
        );

        assertEquals(
                2_000_00,
                account.getCashBalance()
        );

        assertEquals(
                100,
                account.getPortfolio()
                        .getQuantity("AAPL")
        );
    }

    @Test
    void shouldRejectInsufficientCash() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        1_000_000
                );

        assertThrows(
                IllegalStateException.class,
                () -> account.buy(
                        "AAPL",
                        100,
                        18000
                )
        );
    }

    @Test
    void shouldSellShares() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        account.buy(
                "AAPL",
                50,
                18000
        );

        account.sell(
                "AAPL",
                20,
                18000
        );

        assertEquals(
                30,
                account.getPortfolio()
                        .getQuantity("AAPL")
        );
    }

    @Test
    void shouldRejectSellingTooManyShares() {

        TraderAccount account =
                new TraderAccount(
                        1,
                        2_000_000
                );

        assertThrows(
                IllegalStateException.class,
                () -> account.sell(
                        "AAPL",
                        10,
                        18000
                )
        );
    }
}