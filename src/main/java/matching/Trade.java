package matching;

public final class Trade {

    private final long tradeId;

    private final long buyOrderId;
    private final long sellOrderId;

    private final long buyerTraderId;
    private final long sellerTraderId;

    private final String symbol;
    private final long price;
    private final long quantity;

    public Trade(
            long tradeId,
            long buyOrderId,
            long sellOrderId,
            long buyerTraderId,
            long sellerTraderId,
            String symbol,
            long price,
            long quantity
    ) {

        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;

        this.buyerTraderId = buyerTraderId;
        this.sellerTraderId = sellerTraderId;

        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
    }

    public long getTradeId() {
        return tradeId;
    }

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
    }

    public long getBuyerTraderId() {
        return buyerTraderId;
    }

    public long getSellerTraderId() {
        return sellerTraderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {

        return "Trade{" +
                "tradeId=" + tradeId +
                ", buyOrderId=" + buyOrderId +
                ", sellOrderId=" + sellOrderId +
                ", buyerTraderId=" + buyerTraderId +
                ", sellerTraderId=" + sellerTraderId +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}