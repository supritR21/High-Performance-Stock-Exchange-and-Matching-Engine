package book;

import exchange.Instrument;
import order.Order;
import order.OrderSide;
import order.OrderStatus;
import order.OrderType;

public final class OrderBook {

    private final Instrument instrument;
    private final BidBook bidBook;
    private final AskBook askBook;

    public OrderBook(Instrument instrument) {

        this.instrument = instrument;
        this.bidBook = new BidBook();
        this.askBook = new AskBook();
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void addOrder(Order order) {

        if (!order.getSymbol()
                .equals(instrument.getSymbol())) {

            throw new IllegalArgumentException(
                    "Order symbol does not match order book"
            );
        }

        if (order.getType() != OrderType.LIMIT) {
            throw new IllegalArgumentException(
                    "Only LIMIT orders can rest in the order book"
            );
        }

        if (order.getSide() == OrderSide.BUY) {
            bidBook.add(order);
        } else {
            askBook.add(order);
        }
    }

    public boolean cancelOrder(Order order) {

        if (!order.getSymbol()
                .equals(instrument.getSymbol())) {

            throw new IllegalArgumentException(
                    "Order symbol does not match order book"
            );
        }

        if (order.getStatus() == OrderStatus.FILLED) {
            throw new IllegalStateException(
                    "Cannot cancel a filled order"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return false;
        }

        PriceLevel level;

        if (order.getSide() == OrderSide.BUY) {

            level =
                    bidBook.getPriceLevel(
                            order.getPrice()
                    );

        } else {

            level =
                    askBook.getPriceLevel(
                            order.getPrice()
                    );
        }

        if (level == null) {
            return false;
        }

        boolean removed =
                level.removeOrder(order);

        if (!removed) {
            return false;
        }

        if (level.isEmpty()) {

            if (order.getSide() == OrderSide.BUY) {

                bidBook.removePriceLevel(
                        order.getPrice()
                );

            } else {

                askBook.removePriceLevel(
                        order.getPrice()
                );
            }
        }

        order.cancel();

        return true;
    }

    public BidBook getBidBook() {
        return bidBook;
    }

    public AskBook getAskBook() {
        return askBook;
    }
}