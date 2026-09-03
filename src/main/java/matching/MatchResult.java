package matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MatchResult {

    private final List<Trade> trades;

    public MatchResult() {
        this.trades = new ArrayList<>();
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public List<Trade> getTrades() {
        return Collections.unmodifiableList(trades);
    }

    public boolean hasTrades() {
        return !trades.isEmpty();
    }
}