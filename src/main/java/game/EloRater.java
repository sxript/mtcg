package game;

import app.models.Stats;

public class EloRater {

    public void calculateRating(Stats statsA, Stats statsB, boolean isDraw) {
        double expectedValueA = calculateExpectedValue(statsA.getElo(), statsB.getElo());
        double expectedValueB = calculateExpectedValue(statsB.getElo(), statsA.getElo());
        double scoreA = isDraw ? 0.5 : 1;
        double scoreB = isDraw ? 0.5 : 0;
        int kFactorA = calculateKFactor(statsA);
        int kFactorB = calculateKFactor(statsB);

        int updatedRatingA = (int) Math.round(statsA.getElo() + kFactorA * (scoreA - expectedValueA));
        statsA.setElo(updatedRatingA);

        int updatedRatingB = (int) Math.round(statsB.getElo() + kFactorB * (scoreB - expectedValueB));
        statsB.setElo(updatedRatingB);
    }

    private double calculateExpectedValue(double ratingA, double ratingB) {
        return 1 / (1 + Math.pow(10.0, (ratingB - ratingA) / 400));
    }

    private int calculateKFactor(Stats stats) {
        int totalGames = stats.getDraws() + stats.getLosses() + stats.getWins();
        int elo = stats.getElo();
        if(totalGames < 30) {
            return 40;
        } else if (elo < 2400) {
            return 20;
        }
        return 10;
    }
}
