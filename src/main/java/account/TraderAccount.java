package account;

public final class TraderAccount {

    private final long traderId;

    private long cashBalance;
    private long reservedCash;

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
        this.reservedCash = 0;
        this.portfolio = new Portfolio();
    }

    public long getTraderId() {
        return traderId;
    }

    public long getCashBalance() {
        return cashBalance;
    }

    public long getReservedCash() {
        return reservedCash;
    }

    public long getAvailableCash() {
        return cashBalance - reservedCash;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void deposit(long amount) {

        validatePositive(amount, "Deposit");

        cashBalance += amount;
    }

    public void withdraw(long amount) {

        validatePositive(amount, "Withdrawal");

        if (amount > getAvailableCash()) {
            throw new IllegalStateException(
                    "Insufficient available cash"
            );
        }

        cashBalance -= amount;
    }

    public void reserveCash(long amount) {

        validatePositive(
                amount,
                "Reservation"
        );

        if (amount > getAvailableCash()) {
            throw new IllegalStateException(
                    "Insufficient available cash"
            );
        }

        reservedCash += amount;
    }

    public void releaseCash(long amount) {

        validatePositive(
                amount,
                "Release"
        );

        if (amount > reservedCash) {
            throw new IllegalStateException(
                    "Cannot release more cash than reserved"
            );
        }

        reservedCash -= amount;
    }

    public void debit(long amount) {

        validatePositive(
                amount,
                "Debit"
        );

        if (amount > cashBalance) {
            throw new IllegalStateException(
                    "Insufficient cash"
            );
        }

        cashBalance -= amount;
    }

    public void credit(long amount) {

        validatePositive(
                amount,
                "Credit"
        );

        cashBalance += amount;
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

    private void validatePositive(
            long amount,
            String operation
    ) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    operation +
                    " amount must be positive"
            );
        }
    }

    @Override
    public String toString() {

        return "TraderAccount{" +
                "traderId=" + traderId +
                ", cashBalance=" + cashBalance +
                ", reservedCash=" + reservedCash +
                ", availableCash=" + getAvailableCash() +
                ", portfolio=" + portfolio +
                '}';
    }
}