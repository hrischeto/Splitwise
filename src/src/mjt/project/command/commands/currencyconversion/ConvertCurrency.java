package mjt.project.command.commands.currencyconversion;

import mjt.project.command.CommandMessages;
import mjt.project.command.factory.Command;
import mjt.project.currencymanagment.CurrencyManager;
import mjt.project.entities.users.RegisteredUser;
import mjt.project.exceptions.FailedCommandCreationException;
import mjt.project.http.ExchangeRatesClient;

import java.util.Objects;
import java.util.Optional;

public class ConvertCurrency implements Command {
    private static final int INPUT_LENGTH = 1;
    private final String newCurrency;
    private final RegisteredUser user;
    private final CurrencyManager currencyManager;

    public ConvertCurrency(RegisteredUser user, CurrencyManager currencyManager, String... input) {
        validateInput(user, currencyManager, input);

        this.user = user;
        this.currencyManager = currencyManager;

        if (input.length == INPUT_LENGTH) {
            this.newCurrency = input[0];
        } else {
            this.newCurrency = null;
        }
    }

    public String execute() {
        if (Objects.isNull(newCurrency)) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Invalid input for \"convert-currency\" command.\"";
        }

        Optional<String> opCurrency = currencyManager.getCurrencySymbol(newCurrency);

        if (opCurrency.isEmpty()) {
            return CommandMessages.ERROR_MESSAGE + " \"message\":\"Given currency is not available.\"";
        }

        ExchangeRatesClient exchangeRatesClient = currencyManager.getExchangeRatesClient();
        double rate = exchangeRatesClient.getExchangeRate(opCurrency.get());
        user.setCurrentCurrencyRate(rate);

        return CommandMessages.OK_MESSAGE + " \"message\":\"Currency converted successfully to " + newCurrency + ".\"";
    }

    private void validateInput(RegisteredUser user, CurrencyManager currencyManager, String... input) {

        if (Objects.isNull(user)) {
            throw new FailedCommandCreationException("User was null.");
        }
        if (Objects.isNull(currencyManager)) {
            throw new FailedCommandCreationException("Currency manager is null.");
        }
        if (Objects.isNull(input)) {
            throw new FailedCommandCreationException("User input was null");
        }
    }
}
