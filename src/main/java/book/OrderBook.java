package book;

import exchange.Instrument;
import order.Order;
import order.OrderSide;

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

        if (!order.getSymbol().equals(instrument.getSymbol())) {
            throw new IllegalArgumentException(
                    "Order symbol does not match order book"
            );
        }

        if (order.getSide() == OrderSide.BUY) {
            bidBook.add(order);
        } else {
            askBook.add(order);
        }
    }

    public BidBook getBidBook() {
        return bidBook;
    }

    public AskBook getAskBook() {
        return askBook;
    }
}