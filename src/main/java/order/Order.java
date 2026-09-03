package order;

public final class Order {

    private final long orderId;
    private final long traderId;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final long price;
    private final long quantity;

    private long remainingQuantity;
    private OrderStatus status;

    public Order(
            long orderId,
            long traderId,
            String symbol,
            OrderSide side,
            OrderType type,
            long price,
            long quantity
    ) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        if (type == OrderType.LIMIT && price <= 0) {
            throw new IllegalArgumentException(
                    "LIMIT order price must be positive"
            );
        }

        if (type == OrderType.MARKET && price != 0) {
            throw new IllegalArgumentException(
                    "MARKET order price must be 0"
            );
        }

        this.orderId = orderId;
        this.traderId = traderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;

        this.remainingQuantity = quantity;
        this.status = OrderStatus.NEW;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getTraderId() {
        return traderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void accept() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException(
                    "Only NEW orders can be accepted"
            );
        }

        status = OrderStatus.ACCEPTED;
    }

    public void fill(long quantity) {

        if (quantity <= 0 ||
                quantity > remainingQuantity) {

            throw new IllegalArgumentException(
                    "Invalid fill quantity"
            );
        }

        remainingQuantity -= quantity;

        if (remainingQuantity == 0) {
            status = OrderStatus.FILLED;
        } else {
            status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void cancel() {

        if (status == OrderStatus.FILLED) {
            throw new IllegalStateException(
                    "Cannot cancel a filled order"
            );
        }

        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Order is already cancelled"
            );
        }

        status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {

        return "Order{" +
                "orderId=" + orderId +
                ", traderId=" + traderId +
                ", symbol='" + symbol + '\'' +
                ", side=" + side +
                ", type=" + type +
                ", price=" + price +
                ", quantity=" + quantity +
                ", remainingQuantity=" +
                remainingQuantity +
                ", status=" + status +
                '}';
    }
}