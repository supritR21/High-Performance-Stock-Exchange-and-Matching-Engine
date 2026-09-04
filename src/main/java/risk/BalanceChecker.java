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

        if (order.getType() == OrderType.MARKET) {

            if (order.getSide() == OrderSide.BUY) {
                return RiskCheckResult.rejected(
                        "Market BUY requires a maximum risk price"
                );
            }

            return RiskCheckResult.approved();
        }

        if (order.getPrice() <= 0) {
            return RiskCheckResult.rejected(
                    "Limit order price must be positive"
            );
        }

        if (order.getSide() != OrderSide.BUY) {
            return RiskCheckResult.approved();
        }

        final long orderValue;

        try {
            orderValue = Math.multiplyExact(
                    order.getPrice(),
                    order.getQuantity()
            );
        } catch (ArithmeticException e) {

            return RiskCheckResult.rejected(
                    "Order value exceeds supported range"
            );
        }

        // IMPORTANT:
        // Use available cash, not total cash balance.
        if (orderValue > account.getAvailableCash()) {

            return RiskCheckResult.rejected(
                    "Insufficient cash: required=" +
                    orderValue +
                    ", available=" +
                    account.getAvailableCash()
            );
        }

        return RiskCheckResult.approved();
    }
}