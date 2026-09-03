package matching;

import book.AskBook;
import book.BidBook;
import book.OrderBook;
import book.PriceLevel;
import order.Order;
import order.OrderSide;
import order.OrderStatus;
import order.OrderType;

import java.util.concurrent.atomic.AtomicLong;

public final class MatchingEngine {

    private final AtomicLong tradeIdGenerator =
            new AtomicLong(0);

    public MatchResult process(
            OrderBook book,
            Order incomingOrder
    ) {

        MatchResult result =
                new MatchResult();

        if (incomingOrder.getStatus()
                == OrderStatus.NEW) {

            incomingOrder.accept();
        }

        if (incomingOrder.getSide()
                == OrderSide.BUY) {

            matchBuyOrder(
                    book,
                    incomingOrder,
                    result
            );

        } else {

            matchSellOrder(
                    book,
                    incomingOrder,
                    result
            );
        }

        /*
         * MARKET orders never rest in the book.
         *
         * LIMIT orders with remaining quantity
         * become resting orders.
         */
        if (
                incomingOrder.getType()
                        == OrderType.LIMIT
                && incomingOrder.getRemainingQuantity() > 0
        ) {

            book.addOrder(incomingOrder);
        }

        return result;
    }

    private void matchBuyOrder(
            OrderBook book,
            Order buyOrder,
            MatchResult result
    ) {

        AskBook askBook =
                book.getAskBook();

        while (
                buyOrder.getRemainingQuantity() > 0
                && !askBook.isEmpty()
        ) {

            PriceLevel bestAsk =
                    askBook.getBestLevel();

            long askPrice =
                    bestAsk.getPrice();

            /*
             * LIMIT BUY:
             *
             * buyPrice >= askPrice
             *
             * MARKET BUY:
             *
             * always matches the best available ask.
             */
            if (
                    buyOrder.getType()
                            == OrderType.LIMIT
                    && buyOrder.getPrice()
                            < askPrice
            ) {

                break;
            }

            Order sellOrder =
                    bestAsk.getFirstOrder();

            executeTrade(
                    buyOrder,
                    sellOrder,
                    askPrice,
                    result
            );

            if (
                    sellOrder.getRemainingQuantity()
                            == 0
            ) {

                bestAsk.removeFirstOrder();
            }

            if (bestAsk.isEmpty()) {

                askBook.removePriceLevel(
                        askPrice
                );
            }
        }
    }

    private void matchSellOrder(
            OrderBook book,
            Order sellOrder,
            MatchResult result
    ) {

        BidBook bidBook =
                book.getBidBook();

        while (
                sellOrder.getRemainingQuantity() > 0
                && !bidBook.isEmpty()
        ) {

            PriceLevel bestBid =
                    bidBook.getBestLevel();

            long bidPrice =
                    bestBid.getPrice();

            /*
             * LIMIT SELL:
             *
             * sellPrice <= bidPrice
             *
             * MARKET SELL:
             *
             * always matches the best available bid.
             */
            if (
                    sellOrder.getType()
                            == OrderType.LIMIT
                    && sellOrder.getPrice()
                            > bidPrice
            ) {

                break;
            }

            Order buyOrder =
                    bestBid.getFirstOrder();

            executeTrade(
                    buyOrder,
                    sellOrder,
                    bidPrice,
                    result
            );

            if (
                    buyOrder.getRemainingQuantity()
                            == 0
            ) {

                bestBid.removeFirstOrder();
            }

            if (bestBid.isEmpty()) {

                bidBook.removePriceLevel(
                        bidPrice
                );
            }
        }
    }

    private void executeTrade(
            Order buyOrder,
            Order sellOrder,
            long executionPrice,
            MatchResult result
    ) {

        long executionQuantity =
                Math.min(
                        buyOrder.getRemainingQuantity(),
                        sellOrder.getRemainingQuantity()
                );

        buyOrder.fill(executionQuantity);
        sellOrder.fill(executionQuantity);

        Trade trade =
                new Trade(
                        tradeIdGenerator.incrementAndGet(),
                        buyOrder.getOrderId(),
                        sellOrder.getOrderId(),
                        buyOrder.getTraderId(),
                        sellOrder.getTraderId(),
                        buyOrder.getSymbol(),
                        executionPrice,
                        executionQuantity
                );

        result.addTrade(trade);
    }
}