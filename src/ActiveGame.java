import java.io.IOException;

class ActiveGame implements Runnable {
    final Player player1;
    final Player player2;
    final Server server;

    ActiveGame(Player player1, Player player2, Server server) {
        this.player1 = player1;
        this.player2 = player2;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            player1.dataOutputStream.writeUTF(player1.alliance);
            player1.dataOutputStream.writeUTF(player2.name);

            player2.dataOutputStream.writeUTF(player2.alliance);
            player2.dataOutputStream.writeUTF(player1.name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Player curPlayer = player1.alliance.equals("White") ? player1 : player2;

        while (true) {
            Player nextPlayer = getOpponent(curPlayer);

            try {
                String command = curPlayer.dataInputStream.readUTF();

                if (command.equals("end") && nextPlayer.dataInputStream.readUTF().equals("end")) {
                    server.endGame(this);
                    return;
                }

                nextPlayer.dataOutputStream.writeUTF(command);
                if (command.equals("promote")) {
                    String promotionCoinType = curPlayer.dataInputStream.readUTF();
                    nextPlayer.dataOutputStream.writeUTF(promotionCoinType);
                }

                // (row1, col1) -> (row2, col2)
                for (int i = 0; i < 4; i++) {
                    int x = curPlayer.dataInputStream.readInt();
                    nextPlayer.dataOutputStream.writeInt(7 - x);
                }

                curPlayer = nextPlayer;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private Player getOpponent(Player player) {
        return player == player1 ? player2 : player1;
    }
}
