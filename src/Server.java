import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private Player player1;
    private List<ActiveGame> activeGames;

    public Server(final int port) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.printf("Listening to Port %d...\n", port);

        activeGames = new ArrayList<>();

        //noinspection InfiniteLoopStatement
        while (true) {
            Socket s = ss.accept();
            if (player1 == null) {
                player1 = new Player(s, getRandomAlliance());
                System.out.println("Player1 => " + player1.alliance);
            } else {
                Player player2 = new Player(s, getOppositeAlliance(player1.alliance));
                System.out.println("Player2 => " + player2.alliance);
                ActiveGame game = new ActiveGame(player1, player2);
                Thread thread = new Thread(game);
                thread.start();
                activeGames.add(game);
                player1 = null;
            }
        }
    }

    private String getRandomAlliance() {
        return Math.random() < 0.5 ? "White" : "Black";
    }

    private String getOppositeAlliance(String alliance) {
        return alliance.equals("White") ? "Black" : "White";
    }

    public static void main(String[] args) throws IOException {
        new Server(3065);
    }
}
