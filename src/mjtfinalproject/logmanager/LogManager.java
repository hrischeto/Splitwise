package mjtfinalproject.logmanager;

import mjtfinalproject.entities.users.RegisteredUser;

import java.nio.channels.SocketChannel;

public interface LogManager {
    boolean isUserLogged(SocketChannel clientChannel);

    void logUser(SocketChannel clientChannel, RegisteredUser user);

    RegisteredUser getUser(SocketChannel clientChannel);

    void logOutUser(SocketChannel clientChannel);
}
