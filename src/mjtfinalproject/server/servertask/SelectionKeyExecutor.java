package mjtfinalproject.server.servertask;

import mjtfinalproject.command.factory.CommandFactory;
import mjtfinalproject.exceptions.NetworkException;
import mjtfinalproject.server.Server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SelectionKeyExecutor implements Runnable {
    private static final String STOP_COMMAND = "stop";

    private final SelectionKey key;
    private final ByteBuffer buffer;
    private final CommandFactory commandFactory;
    private final Selector selector;
    private final Server server;

    public SelectionKeyExecutor(SelectionKey key, ByteBuffer buffer, CommandFactory commandFactory, Selector selector,
                                Server server) {
        validateArguments(key, buffer, commandFactory, selector, server);

        this.key = key;
        this.buffer = buffer;
        this.commandFactory = commandFactory;
        this.selector = selector;
        this.server = server;
    }

    @Override
    public void run() {
        try (selector) {
            if (key.isReadable()) {
                read(key);
            } else if (key.isAcceptable()) {
                accept(selector, key);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);
        if (clientInput == null) {
            return;
        }

        String output = commandFactory.newCommand(clientInput, clientChannel).execute();
        if (output.equals(STOP_COMMAND)) {
            server.stop();
            return;
        }

        writeClientOutput(clientChannel, output);

    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    void validateArguments(SelectionKey key, ByteBuffer buffer, CommandFactory commandFactory, Selector selector,
                           Server server) {
        if (Objects.isNull(key)) {
            throw new NetworkException("Selection key was null.");
        }

        if (Objects.isNull(buffer)) {
            throw new NetworkException("Buffer was null.");
        }

        if (Objects.isNull(commandFactory)) {
            throw new NetworkException("Command factory was null.");
        }

        if (Objects.isNull(selector)) {
            throw new NetworkException("Selector was null.");
        }

        if (Objects.isNull(server)) {
            throw new NetworkException("Server was null.");
        }
    }
}
