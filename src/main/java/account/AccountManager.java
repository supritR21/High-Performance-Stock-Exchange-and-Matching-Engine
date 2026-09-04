package account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AccountManager {

    private final Map<Long, TraderAccount> accounts;

    public AccountManager() {
        this.accounts = new ConcurrentHashMap<>();
    }

    public void register(
            TraderAccount account
    ) {

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null"
            );
        }

        TraderAccount existing =
                accounts.putIfAbsent(
                        account.getTraderId(),
                        account
                );

        if (existing != null) {
            throw new IllegalStateException(
                    "Trader already exists: " +
                    account.getTraderId()
            );
        }
    }

    public TraderAccount get(
            long traderId
    ) {

        return accounts.get(traderId);
    }

    public boolean exists(
            long traderId
    ) {

        return accounts.containsKey(
                traderId
        );
    }

    public int size() {
        return accounts.size();
    }
}