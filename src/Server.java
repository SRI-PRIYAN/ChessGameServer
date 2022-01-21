import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private Player player1;
    private final List<ActiveGame> activeGames;

    public Server(final int port) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.printf("Listening to Port %d...\n", port);

        activeGames = new ArrayList<>();

        //noinspection InfiniteLoopStatement
        while (true) {
            Socket s = ss.accept();
            if (player1 == null) {
                player1 = new Player(s, getRandomAlliance());
            } else {
                Player player2 = new Player(s, getOppositeAlliance(player1.alliance));
                System.out.printf("\nStarting Game %s vs %s\n", player1.name, player2.name);
                ActiveGame game = new ActiveGame(player1, player2, this);
                Thread thread = new Thread(game);
                thread.start();
                activeGames.add(game);
                System.out.printf("\nNumber Of Active Games = %d\n", activeGames.size());
                player1 = null;
            }
        }
    }

    public void endGame(ActiveGame game) {
        try {
            game.player1.socket.close();
            game.player2.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("\nEnding Game %s vs %s\n", game.player1.name, game.player2.name);
        activeGames.remove(game);
        System.out.printf("\nNumber Of Active Games = %d\n", activeGames.size());
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
