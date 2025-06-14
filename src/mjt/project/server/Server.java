package mjt.project.server;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mjt.project.command.factory.CommandFactory;
import mjt.project.repositories.grouprepository.GroupRepository;
import mjt.project.repositories.grouprepository.InMemoryGroupRepository;
import mjt.project.repositories.userrepository.InMemoryUserRepository;
import mjt.project.repositories.userrepository.UserRepository;
import mjt.project.server.servertask.ReadExecutor;

public class Server {

    private static final String USER_DATABASE = "users.txt";
    private static final String GROUP_DATABASE = "groups.txt";

    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private final int port;

    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final CommandFactory commandFactory;

    public Server(int port) {
        validatePort(port);

        groupRepository = readGroupRepository();
        userRepository = readUserRepository();

        commandFactory = new CommandFactory(groupRepository, userRepository);

        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             ExecutorService executor = Executors.newCachedThreadPool()
        ) {
            configureServerSocketChannel(serverSocketChannel);
            isServerWorking = true;
            while (isServerWorking) {
                handleClientRequests(selector, executor);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    private void handleClientRequests(Selector selector, Executor executor) throws IOException {
        try {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                return;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                try {
                    if (key.isReadable()) {
                        if (!read(key, executor)) {
                            continue;
                        }
                    } else if (key.isAcceptable()) {
                        accept(selector, key);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e.getMessage(), e);
                }

                keyIterator.remove();
            }
        } catch (IOException | UncheckedIOException e) {
            System.out.println("Error occurred while processing client request: " + e.getMessage());
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }

        groupRepository.safeToDatabase(GROUP_DATABASE);
        userRepository.safeToDatabase(USER_DATABASE);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept;
        do {
            accept = sockChannel.accept();
        } while (accept == null);

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

    }

    private boolean read(SelectionKey key, Executor executor) throws IOException {

        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            String clientInput = getClientInput(clientChannel);
            if (clientInput == null) {
                return false;
            }

            executor.execute(new ReadExecutor(buffer, commandFactory, this, clientInput, clientChannel));
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel) throws IOException {
        selector = Selector.open();

        channel.bind(new InetSocketAddress(HOST, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        isServerWorking = true;
    }

    private GroupRepository readGroupRepository() {
        Path database = Path.of(GROUP_DATABASE);
        if (Files.notExists(database)) {
            return new InMemoryGroupRepository();
        }

        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(database))) {
            Object groupRepository;
            groupRepository = objectInputStream.readObject();

            return (GroupRepository) groupRepository;

        } catch (EOFException e) {
//
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException("The files does not exist", e);
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading from a file", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private UserRepository readUserRepository() {
        Path database = Path.of(USER_DATABASE);
        if (Files.notExists(database)) {
            return new InMemoryUserRepository();
        }

        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(database))) {
            Object userRepository;
            userRepository = objectInputStream.readObject();

            if (userRepository != null) {
                return (UserRepository) userRepository;
            }

        } catch (EOFException e) {
//
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException("The files does not exist", e);
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading from a file", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private void validatePort(int port) {
        final int minPort = 1024;

        if (port < minPort) {
            throw new IllegalArgumentException("Unavailable port.");
        }
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        if (readBytes == 0) {
            return null;
        }
        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

}
