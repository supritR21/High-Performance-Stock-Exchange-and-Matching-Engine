package account;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Portfolio {

    private final Map<String, Position> positions;

    public Portfolio() {
        this.positions = new HashMap<>();
    }

    public Position getPosition(String symbol) {

        return positions.get(symbol);
    }

    public Position getOrCreatePosition(String symbol) {

        return positions.computeIfAbsent(
                symbol,
                Position::new
        );
    }

    public long getQuantity(String symbol) {

        Position position =
                positions.get(symbol);

        if (position == null) {
            return 0;
        }

        return position.getQuantity();
    }

    public void addPosition(
            String symbol,
            long quantity
    ) {

        getOrCreatePosition(symbol)
                .increase(quantity);
    }

    public void removePosition(
            String symbol,
            long quantity
    ) {

        Position position =
                positions.get(symbol);

        if (position == null) {
            throw new IllegalStateException(
                    "No position exists for " + symbol
            );
        }

        position.decrease(quantity);

        /*
         * Remove empty positions from the map.
         */
        if (position.getQuantity() == 0) {
            positions.remove(symbol);
        }
    }

    public Map<String, Position> getPositions() {

        return Collections.unmodifiableMap(
                positions
        );
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "positions=" + positions +
                '}';
    }
}