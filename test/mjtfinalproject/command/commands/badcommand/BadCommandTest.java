package mjtfinalproject.command.commands.badcommand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BadCommandTest {
    private final BadCommand badCommand = new BadCommand();

    @Test
    void testReturnsErrorMessage() {
        assertTrue(badCommand.execute().contains("\"status\":\"ERROR\""),"A negative message should always be returned.");
    }
}
