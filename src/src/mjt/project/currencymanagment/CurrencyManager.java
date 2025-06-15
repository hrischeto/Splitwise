package mjt.project.currencymanagment;

import mjt.project.http.ExchangeRatesClient;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CurrencyManager {
    public static final double DEFAULT_CURRENCY_RATE = 1.0;
    public static final String DEFAULT_CURRENCY = "EUR";


    private final Map<String, String> currencySymbols;
    private final ExchangeRatesClient exchangeRatesClient;

    public CurrencyManager() {
        exchangeRatesClient = new ExchangeRatesClient();
        currencySymbols = exchangeRatesClient.getAvailableSymbols();
    }

    public Optional<String> getCurrencySymbol(String clientInput) {
        if (Objects.isNull(clientInput)) {
            throw new IllegalArgumentException("Client input is null.");
        }

        if (!currencySymbols.containsKey(clientInput)) {
            for (String key : currencySymbols.keySet()) {
                if (currencySymbols.get(key).equals(clientInput)) {
                    return Optional.of(key);
                }
            }

            return Optional.empty();
        } else {
            return Optional.of(clientInput);
        }
    }

    public ExchangeRatesClient getExchangeRatesClient() {
        return exchangeRatesClient;
    }
}
