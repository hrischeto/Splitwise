package mjt.project.server.servertask;

import mjt.project.command.commands.server.StopServer;
import mjt.project.command.factory.CommandFactory;
import mjt.project.exceptions.NetworkException;
import mjt.project.server.Server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class ReadExecutor implements Runnable {

    private final ByteBuffer buffer;
    private final CommandFactory commandFactory;
    private final Server server;
    private final String clientInput;
    private final SocketChannel clientChannel;

    public ReadExecutor(ByteBuffer buffer, CommandFactory commandFactory, Server server, String clientInput,
                        SocketChannel clientChannel) {
        validateArguments(buffer, commandFactory, server, clientInput, clientChannel);

        this.buffer = buffer;
        this.commandFactory = commandFactory;
        this.server = server;
        this.clientInput = clientInput;
        this.clientChannel = clientChannel;
    }

    @Override
    public void run() {
        try {
            String output = commandFactory.newCommand(clientInput, clientChannel).execute();
            if (output.equals(StopServer.STOP_COMMAND)) {
                server.stop();
            }

            writeClientOutput(clientChannel, output);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    void validateArguments(ByteBuffer buffer, CommandFactory commandFactory, Server server, String clientInput,
                           SocketChannel clientChannel) {
        if (Objects.isNull(buffer)) {
            throw new NetworkException("Buffer was null.");
        }

        if (Objects.isNull(commandFactory)) {
            throw new NetworkException("Command factory was null.");
        }

        if (Objects.isNull(server)) {
            throw new NetworkException("Server was null.");
        }
        if (Objects.isNull(clientChannel)) {
            throw new NetworkException("Client channel was null.");
        }
        if (Objects.isNull(clientInput)) {
            throw new NetworkException("Client input was null.");
        }
    }

}
