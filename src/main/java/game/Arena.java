package game;

public class Arena {
    private static final int MAX_ROUNDS = 100;
    public void battle(User player1, User player2) {
        for (int i = 0; i < MAX_ROUNDS; i++) {
            player1.drawCard();

        }
    }

    private void updateScore(User player1, User player2, boolean isP1Winner) {

    }


}
