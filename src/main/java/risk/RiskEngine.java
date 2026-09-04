package risk;

import account.TraderAccount;
import order.Order;
import order.OrderStatus;

public final class RiskEngine {

    private final BalanceChecker balanceChecker;
    private final PositionChecker positionChecker;

    public RiskEngine() {

        this.balanceChecker =
                new BalanceChecker();

        this.positionChecker =
                new PositionChecker();
    }

    public RiskCheckResult check(
            TraderAccount account,
            Order order
    ) {

        /*
         * Basic validation
         */
        if (account == null) {

            return RiskCheckResult.rejected(
                    "Trader account does not exist"
            );
        }

        if (order == null) {

            return RiskCheckResult.rejected(
                    "Order cannot be null"
            );
        }

        if (account.getTraderId()
                != order.getTraderId()) {

            return RiskCheckResult.rejected(
                    "Order trader does not match account"
            );
        }

        /*
         * Don't risk-check an order that has already
         * reached a terminal state.
         */
        if (order.getStatus() == OrderStatus.CANCELLED) {

            return RiskCheckResult.rejected(
                    "Cancelled order cannot be submitted"
            );
        }

        if (order.getStatus() == OrderStatus.FILLED) {

            return RiskCheckResult.rejected(
                    "Filled order cannot be submitted"
            );
        }

        /*
         * Position check.
         */
        RiskCheckResult positionResult =
                positionChecker.check(
                        account,
                        order
                );

        if (!positionResult.isApproved()) {
            return positionResult;
        }

        /*
         * Cash check.
         */
        RiskCheckResult balanceResult =
                balanceChecker.check(
                        account,
                        order
                );

        if (!balanceResult.isApproved()) {
            return balanceResult;
        }

        return RiskCheckResult.approved();
    }
}