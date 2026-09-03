package account;

public final class TraderAccount {

    private final long traderId;

    /*
     * Money is represented in the smallest unit.
     *
     * Example:
     *
     * $10,000.00 → 1,000,000 cents
     */
    private long cashBalance;

    private final Portfolio portfolio;

    public TraderAccount(
            long traderId,
            long initialCash
    ) {

        if (traderId <= 0) {
            throw new IllegalArgumentException(
                    "Trader ID must be positive"
            );
        }

        if (initialCash < 0) {
            throw new IllegalArgumentException(
                    "Initial cash cannot be negative"
            );
        }

        this.traderId = traderId;
        this.cashBalance = initialCash;
        this.portfolio = new Portfolio();
    }

    public long getTraderId() {
        return traderId;
    }

    public long getCashBalance() {
        return cashBalance;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void deposit(long amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit must be positive"
            );
        }

        cashBalance += amount;
    }

    public void withdraw(long amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal must be positive"
            );
        }

        if (amount > cashBalance) {
            throw new IllegalStateException(
                    "Insufficient cash"
            );
        }

        cashBalance -= amount;
    }

    public void credit(long amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Credit must be positive"
            );
        }

        cashBalance += amount;
    }

    public void debit(long amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Debit must be positive"
            );
        }

        if (amount > cashBalance) {
            throw new IllegalStateException(
                    "Insufficient cash"
            );
        }

        cashBalance -= amount;
    }

    public void buy(
            String symbol,
            long quantity,
            long price
    ) {

        if (quantity <= 0 || price <= 0) {
            throw new IllegalArgumentException(
                    "Invalid buy parameters"
            );
        }

        long totalValue =
                Math.multiplyExact(
                        quantity,
                        price
                );

        debit(totalValue);

        portfolio.addPosition(
                symbol,
                quantity
        );
    }

    public void sell(
            String symbol,
            long quantity,
            long price
    ) {

        if (quantity <= 0 || price <= 0) {
            throw new IllegalArgumentException(
                    "Invalid sell parameters"
            );
        }

        long totalValue =
                Math.multiplyExact(
                        quantity,
                        price
                );

        portfolio.removePosition(
                symbol,
                quantity
        );

        credit(totalValue);
    }

    @Override
    public String toString() {

        return "TraderAccount{" +
                "traderId=" + traderId +
                ", cashBalance=" + cashBalance +
                ", portfolio=" + portfolio +
                '}';
    }
}