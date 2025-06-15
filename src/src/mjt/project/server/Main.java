package mjt.project.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(6789);
        server.start();
    }
}
