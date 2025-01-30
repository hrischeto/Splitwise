package mjtfinalproject.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import mjtfinalproject.command.factory.CommandFactory;

public class Server {

    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private static final int PORT = 6789;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;


    public Server() {
        validateArguments(port, pollRepository);

        this.pollRepository = pollRepository;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            configureServerSocketChannel(serverSocketChannel);
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            if (!read(key)) {
                                continue;
                            }
                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
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

    private boolean read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);
        if (clientInput == null) {
            return false;
        }

        String output = (CommandFactory.newCommand(clientInput)).execute(pollRepository);
        writeClientOutput(clientChannel, output);

        return true;
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void configureServerSocketChannel(ServerSocketChannel channel) throws IOException {
        selector = Selector.open();

        channel.bind(new InetSocketAddress(HOST, PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        isServerWorking = true;
    }

    private void validateArguments(int port, PollRepository repository) {
        if (port < 0) {
            throw new IllegalArgumentException("Port number should be positive.");
        }

        if (Objects.isNull(repository)) {
            throw new IllegalArgumentException("Poll repository is null.");
        }
    }
}
