package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import game.Deck;
import game.Profile;
import game.Stats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private static final int START_COINS = 20;

//    @JsonAlias({"id"})
//    private String id;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"username"})
    private String username;

    @JsonAlias({"password"})
    private String password;

    @JsonAlias({"coins"})
    private int coins;

    @JsonAlias({"stack"})
    private List<String> stack;

    @JsonAlias({"deck"})
    private Deck deck;

    @JsonAlias({"stats"})
    private Stats stats;

    @JsonAlias({"profile"})
    private Profile profile;

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
//    public Card drawCard() {
//        return deck.getDeck().get(rnd.nextInt(deck.getDeck().size()));
//    }
//
//    public boolean removeCard(Card card) {
//        return deck.getDeck().remove(card);
//    }
//
//    public void addCard(Card card) {
//        deck.getDeck().add(card);
//    }
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
                ", profile=" + profile +
                '}';
    }
}
