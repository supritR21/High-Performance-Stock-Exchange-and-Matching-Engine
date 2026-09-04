package risk;

import account.TraderAccount;
import order.Order;
import order.OrderSide;
import order.OrderType;

public final class BalanceChecker {

    public RiskCheckResult check(
            TraderAccount account,
            Order order
    ) {

        /*
         * Market orders currently don't have a meaningful
         * maximum price in our Order model.
         *
         * Therefore we cannot safely determine the maximum
         * cash requirement yet.
         */
        if (order.getType() == OrderType.MARKET) {

            if (order.getSide() == OrderSide.BUY) {

                return RiskCheckResult.rejected(
                        "Market BUY requires a maximum risk price"
                );
            }

            /*
             * Market SELL can theoretically be checked against
             * position only. Cash isn't required to sell.
             */
            return RiskCheckResult.approved();
        }

        if (order.getPrice() <= 0) {

            return RiskCheckResult.rejected(
                    "Limit order price must be positive"
            );
        }

        if (order.getSide() != OrderSide.BUY) {

            /*
             * SELL orders don't require cash.
             */
            return RiskCheckResult.approved();
        }

        final long orderValue;

        try {

            orderValue =
                    Math.multiplyExact(
                            order.getPrice(),
                            order.getQuantity()
                    );

        } catch (ArithmeticException e) {

            return RiskCheckResult.rejected(
                    "Order value exceeds supported range"
            );
        }

        if (orderValue > account.getCashBalance()) {

            return RiskCheckResult.rejected(
                    "Insufficient cash: required=" +
                    orderValue +
                    ", available=" +
                    account.getCashBalance()
            );
        }

        return RiskCheckResult.approved();
    }
}