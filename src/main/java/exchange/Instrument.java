package exchange;

public final class Instrument {

    private final String symbol;
    private final String companyName;

    public Instrument(String symbol, String companyName) {
        this.symbol = symbol;
        this.companyName = companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String toString() {
        return symbol + " (" + companyName + ")";
    }
}