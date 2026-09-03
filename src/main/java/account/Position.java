package account;

public final class Position {

    private final String symbol;
    private long quantity;

    public Position(String symbol) {
        this.symbol = symbol;
        this.quantity = 0;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getQuantity() {
        return quantity;
    }

    public void increase(long quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        this.quantity += quantity;
    }

    public void decrease(long quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        if (quantity > this.quantity) {
            throw new IllegalStateException(
                    "Insufficient position for " + symbol
            );
        }

        this.quantity -= quantity;
    }

    @Override
    public String toString() {
        return "Position{" +
                "symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}