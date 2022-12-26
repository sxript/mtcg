package game;

import game.Card;

import java.util.ArrayList;

public class Package {
    private static final int COST = 5;
    private static final int SIZE = 5;
    private final ArrayList<Card> cards;

    public Package(ArrayList<Card> cards) {
        this.cards = cards;
    }

}
