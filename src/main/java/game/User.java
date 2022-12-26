package game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Setter
@Getter
public class User {
    private static final Random rnd = new Random();
    private static final int START_COINS = 20;
    private String name;
    private String username;
    private String password;
    private int coins;
    private List<Card> stack = new ArrayList<>();
    private Deck deck;
    private Stats stats;
    private Profile profile;


    public User(String name, String username, String password, int coins, List<Card> stack, Deck deck, Stats stats, Profile profile) {
        setName(name);
        setUsername(username);
        setPassword(password);
        setCoins(coins);
        setStack(stack);
        setDeck(deck);
        setStats(stats);
        setProfile(profile);
    }

    public User(String name, String username, String password, int coins, Stats stats, Profile profile) {
        setName(name);
        setUsername(username);
        setPassword(password);
        setCoins(coins);
        setStats(stats);
        setProfile(profile);
    }

    public User(String username, String password) {
        this(null, username, password, START_COINS, new ArrayList<>(), new Deck(), new Stats(), new Profile());
    }


    public Card drawCard() {
        return deck.getDeck().get(rnd.nextInt(deck.getDeck().size()));
    }

    public boolean removeCard(Card card) {
        return deck.getDeck().remove(card);
    }

    public void addCard(Card card) {
        deck.getDeck().add(card);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", coins=" + coins +
                ", stack=" + stack +
                ", deck=" + deck +
                ", stats=" + stats +
                '}';
    }
}
