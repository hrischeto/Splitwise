package mjt.project.command.commands.badcommand;

import mjt.project.command.CommandMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BadCommandTest {
    private final BadCommand badCommand = new BadCommand();

    @Test
    void testReturnsErrorMessage() {
        assertTrue(badCommand.execute().contains(CommandMessages.ERROR_MESSAGE.toString()),"A negative message should always be returned.");
    }
}
