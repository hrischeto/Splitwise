package mjt.project.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mjt.project.currencymanagment.CurrencyManager;
import mjt.project.exceptions.FailedRequestException;
import mjt.project.http.models.LatestRatesResponse;
import mjt.project.http.models.SymbolsResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;

public class ExchangeRatesClient {

    private static final String BASE = "https://api.exchangeratesapi.io/v1/";
    private static final String SYMBOLS_ENDPOINT = "symbols";
    private static final String LATEST_ENDPOINT = "latest";
    private static final String API_KEY = System.getenv("ExchangeRatesAPIKey");

    private final Gson gson;
    private final HttpClient httpClient;

    public ExchangeRatesClient() {
        gson = new Gson();
        httpClient = HttpClient.newBuilder().build();
    }

    public Map<String, String> getAvailableSymbols() {
        HttpResponse<String> response;

        String urlString = BASE + SYMBOLS_ENDPOINT + "?access_key=" + API_KEY;

        try {
            URI uri = new URI(urlString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new FailedRequestException("Error while creating URI.", e);
        } catch (IOException | InterruptedException e) {
            throw new FailedRequestException("Could not retrieve available symbols.", e);
        }

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            SymbolsResponse symbolsResponse = gson.fromJson(response.body(), SymbolsResponse.class);

            if (symbolsResponse.success()) {
                return symbolsResponse.symbols();
            }
        }

        return null;
    }

    public double getExchangeRate(String currency) {
        HttpResponse<String> response;

        try {
            response = httpClient.send(getLatestRequest(currency), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FailedRequestException("Could not retrieve new currency rate.", e);
        }

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            LatestRatesResponse ratesResponse = gson.fromJson(response.body(), LatestRatesResponse.class);

            if (ratesResponse.success()) {
                return ratesResponse.rates().get(currency);
            }
        }

        return -1.0;
    }

    private HttpRequest getLatestRequest(String currency) {
        if (Objects.isNull(currency)) {
            throw new IllegalArgumentException("Null currency to convert to.");
        }

        String urlString =
            BASE + LATEST_ENDPOINT + "?access_key=" + API_KEY + "&base=" + CurrencyManager.DEFAULT_CURRENCY +
                "&symbols=" + currency;

        HttpRequest request;
        try {
            URI uri = new URI(urlString);
            request = HttpRequest.newBuilder().uri(uri).build();
        } catch (URISyntaxException e) {
            throw new FailedRequestException("Error while creating URI.", e);
        }

        return request;
    }

}