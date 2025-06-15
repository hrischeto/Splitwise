package mjt.project.server.servertask;

import mjt.project.repositories.Repository;
import mjt.project.server.Server;

import java.time.Duration;
import java.util.Objects;

public class RepositorySaver extends Thread {

    private static final int SLEEP_PERIOD = 10;
    private final Repository userRepository;
    private final Repository groupRepository;

    public RepositorySaver(Repository userRepo, Repository groupRepo) {
        if (Objects.isNull(userRepo)) {
            throw new IllegalArgumentException("Null user repository.");
        }
        if (Objects.isNull(groupRepo)) {
            throw new IllegalArgumentException("Null group repository.");
        }

        this.groupRepository = groupRepo;
        this.userRepository = userRepo;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            userRepository.safeToDatabase(Server.USER_DATABASE);
            groupRepository.safeToDatabase(Server.GROUP_DATABASE);

            try {
                sleep(Duration.ofSeconds(SLEEP_PERIOD));
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted.");
            }
        }
    }

}
