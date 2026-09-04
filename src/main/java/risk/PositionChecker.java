package risk;

import account.TraderAccount;
import order.Order;
import order.OrderSide;

public final class PositionChecker {

    public RiskCheckResult check(
            TraderAccount account,
            Order order
    ) {

        /*
         * BUY orders don't require an existing position.
         */
        if (order.getSide() == OrderSide.BUY) {
            return RiskCheckResult.approved();
        }

        long availableShares =
                account.getPortfolio()
                        .getQuantity(
                                order.getSymbol()
                        );

        if (order.getQuantity() > availableShares) {

            return RiskCheckResult.rejected(
                    "Insufficient position: required=" +
                    order.getQuantity() +
                    ", available=" +
                    availableShares +
                    ", symbol=" +
                    order.getSymbol()
            );
        }

        return RiskCheckResult.approved();
    }
}