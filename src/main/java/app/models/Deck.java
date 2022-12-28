package app.models;

import app.models.Card;

import java.util.ArrayList;

public class Deck {
    private static final int DECK_SIZE = 4;
    private ArrayList<Card> deck;


    public void setDeck(ArrayList<Card> cards) {
        if (cards.size() == DECK_SIZE) {
            deck = cards;
        }
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }
}
