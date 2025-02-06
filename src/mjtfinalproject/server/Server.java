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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mjtfinalproject.command.factory.CommandFactory;
import mjtfinalproject.repositories.grouprepository.GroupRepository;
import mjtfinalproject.repositories.grouprepository.InMemoryGroupRepository;
import mjtfinalproject.repositories.userrepository.InMemoryUserRepository;
import mjtfinalproject.repositories.userrepository.UserRepository;
import mjtfinalproject.server.servertask.SelectionKeyExecutor;

public class Server {

    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private static final String ADMIN_USERNAME = System.getenv("SplitwiseAdminUsername");
    private static final String ADMIN_PASSWORD = System.getenv("SplitwiseAdminPassword");

    private final int port;

    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final CommandFactory commandFactory;

    public Server(int port) {
        validatePort(port);

        groupRepository = new InMemoryGroupRepository();
        userRepository = new InMemoryUserRepository();

        commandFactory = new CommandFactory(groupRepository, userRepository);

        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             ExecutorService executor = Executors.newCachedThreadPool();
        ) {
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

                        executor.execute(new SelectionKeyExecutor(key, buffer, commandFactory, selector));

                        keyIterator.remove();
                    }
                } catch (IOException | UncheckedIOException e ) {
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

        groupRepository.safeToDatabase();
        userRepository.safeToDatabase();
    }

    private void configureServerSocketChannel(ServerSocketChannel channel) throws IOException {
        selector = Selector.open();

        channel.bind(new InetSocketAddress(HOST, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        isServerWorking = true;
    }

    private void validatePort(int port) {
        final int minPort = 1024;

        if (port < minPort) {
            throw new IllegalArgumentException("Unavailable port.");
        }
    }

}
