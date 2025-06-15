package mjt.project.logmanager;

import mjt.project.entities.users.RegisteredUser;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelLogManager implements LogManager {

    private final Map<SocketChannel, RegisteredUser> loggedUsers;

    public ParallelLogManager() {
        loggedUsers = new ConcurrentHashMap<>();
    }

    public ParallelLogManager(Map<SocketChannel, RegisteredUser> loggedUsers) {
        this.loggedUsers = loggedUsers;
    }

    @Override
    public boolean isUserLogged(SocketChannel clientChannel) {
        validateChannel(clientChannel);

        return loggedUsers.containsKey(clientChannel);
    }

    @Override
    public void logUser(SocketChannel clientChannel, RegisteredUser user) {
        validateChannel(clientChannel);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("Null user.");
        }

        loggedUsers.put(clientChannel, user);
    }

    @Override
    public RegisteredUser getUser(SocketChannel clientChannel) {
        validateChannel(clientChannel);

        if (isUserLogged(clientChannel)) {
            return loggedUsers.get(clientChannel);
        } else {
            return null;
        }
    }

    @Override
    public void logOutUser(SocketChannel clientChannel) {
        validateChannel(clientChannel);

        if (isUserLogged(clientChannel)) {
            loggedUsers.remove(clientChannel);
        }
    }

    private void validateChannel(SocketChannel clientChannel) {
        if (Objects.isNull(clientChannel)) {
            throw new IllegalArgumentException("Null client channel.");
        }
    }
}
