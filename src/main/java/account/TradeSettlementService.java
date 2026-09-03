package account;

import matching.Trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TradeSettlementService {

    public void settle(
            Trade trade,
            Map<Long, TraderAccount> accounts
    ) {

        TraderAccount buyer =
                getAccount(
                        accounts,
                        trade.getBuyerTraderId()
                );

        TraderAccount seller =
                getAccount(
                        accounts,
                        trade.getSellerTraderId()
                );

        long tradeValue =
                Math.multiplyExact(
                        trade.getPrice(),
                        trade.getQuantity()
                );

        /*
         * Validate everything BEFORE modifying
         * either account.
         */
        if (buyer.getCashBalance() < tradeValue) {
            throw new IllegalStateException(
                    "Buyer has insufficient cash"
            );
        }

        long sellerShares =
                seller.getPortfolio()
                        .getQuantity(
                                trade.getSymbol()
                        );

        if (sellerShares < trade.getQuantity()) {
            throw new IllegalStateException(
                    "Seller has insufficient shares"
            );
        }

        /*
         * Apply settlement.
         */
        buyer.debit(tradeValue);

        buyer.getPortfolio()
                .addPosition(
                        trade.getSymbol(),
                        trade.getQuantity()
                );

        seller.getPortfolio()
                .removePosition(
                        trade.getSymbol(),
                        trade.getQuantity()
                );

        seller.credit(tradeValue);
    }

    public void settleAll(
            List<Trade> trades,
            Map<Long, TraderAccount> accounts
    ) {

        /*
         * First calculate all account deltas.
         *
         * This lets us validate the entire batch before
         * modifying account state.
         */
        Map<Long, Long> cashDeltas =
                new HashMap<>();

        Map<Long, Map<String, Long>> positionDeltas =
                new HashMap<>();

        for (Trade trade : trades) {

            long value =
                    Math.multiplyExact(
                            trade.getPrice(),
                            trade.getQuantity()
                    );

            /*
             * Buyer:
             * cash decreases
             * shares increase
             */
            cashDeltas.merge(
                    trade.getBuyerTraderId(),
                    -value,
                    Long::sum
            );

            positionDeltas
                    .computeIfAbsent(
                            trade.getBuyerTraderId(),
                            id -> new HashMap<>()
                    )
                    .merge(
                            trade.getSymbol(),
                            trade.getQuantity(),
                            Long::sum
                    );

            /*
             * Seller:
             * cash increases
             * shares decrease
             */
            cashDeltas.merge(
                    trade.getSellerTraderId(),
                    value,
                    Long::sum
            );

            positionDeltas
                    .computeIfAbsent(
                            trade.getSellerTraderId(),
                            id -> new HashMap<>()
                    )
                    .merge(
                            trade.getSymbol(),
                            -trade.getQuantity(),
                            Long::sum
                    );
        }

        /*
         * Validate every account before applying changes.
         */
        for (Map.Entry<Long, Long> entry :
                cashDeltas.entrySet()) {

            TraderAccount account =
                    getAccount(
                            accounts,
                            entry.getKey()
                    );

            long newBalance =
                    account.getCashBalance()
                            + entry.getValue();

            if (newBalance < 0) {
                throw new IllegalStateException(
                        "Account " +
                        account.getTraderId() +
                        " has insufficient cash"
                );
            }
        }

        for (Map.Entry<
                Long,
                Map<String, Long>
                > accountEntry :
                positionDeltas.entrySet()) {

            TraderAccount account =
                    getAccount(
                            accounts,
                            accountEntry.getKey()
                    );

            for (Map.Entry<String, Long> positionEntry :
                    accountEntry.getValue().entrySet()) {

                long current =
                        account.getPortfolio()
                                .getQuantity(
                                        positionEntry.getKey()
                                );

                long newQuantity =
                        current +
                        positionEntry.getValue();

                if (newQuantity < 0) {
                    throw new IllegalStateException(
                            "Account " +
                            account.getTraderId() +
                            " has insufficient " +
                            positionEntry.getKey() +
                            " shares"
                    );
                }
            }
        }

        /*
         * Everything is valid.
         * Now apply the changes.
         */
        for (Trade trade : trades) {

            long value =
                    Math.multiplyExact(
                            trade.getPrice(),
                            trade.getQuantity()
                    );

            TraderAccount buyer =
                    getAccount(
                            accounts,
                            trade.getBuyerTraderId()
                    );

            TraderAccount seller =
                    getAccount(
                            accounts,
                            trade.getSellerTraderId()
                    );

            buyer.debit(value);

            buyer.getPortfolio()
                    .addPosition(
                            trade.getSymbol(),
                            trade.getQuantity()
                    );

            seller.getPortfolio()
                    .removePosition(
                            trade.getSymbol(),
                            trade.getQuantity()
                    );

            seller.credit(value);
        }
    }

    private TraderAccount getAccount(
            Map<Long, TraderAccount> accounts,
            long traderId
    ) {

        TraderAccount account =
                accounts.get(traderId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Unknown trader: " + traderId
            );
        }

        return account;
    }
}