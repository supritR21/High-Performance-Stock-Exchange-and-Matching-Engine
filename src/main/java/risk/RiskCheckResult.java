package risk;

public final class RiskCheckResult {

    private final boolean approved;
    private final String reason;

    private RiskCheckResult(
            boolean approved,
            String reason
    ) {
        this.approved = approved;
        this.reason = reason;
    }

    public static RiskCheckResult approved() {
        return new RiskCheckResult(
                true,
                "Order approved"
        );
    }

    public static RiskCheckResult rejected(
            String reason
    ) {
        return new RiskCheckResult(
                false,
                reason
        );
    }

    public boolean isApproved() {
        return approved;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RiskCheckResult{" +
                "approved=" + approved +
                ", reason='" + reason + '\'' +
                '}';
    }
}