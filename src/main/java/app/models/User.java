package app.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import game.Deck;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private static final int START_COINS = 20;

//    @JsonAlias({"id"})
//    String id;

    @JsonAlias({"Name"})
    String name;

    @JsonAlias({"Username"})
    String username;

    @JsonAlias({"Password"})
    String password;

    @JsonAlias({"Coins"})
    @JsonSetter(nulls = Nulls.SKIP)
    int coins = START_COINS;

    @JsonAlias({"Stack"})
    List<String> stack;

    @JsonAlias({"Deck"})
    Deck deck;

    @JsonAlias({"Stats"})
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonUnwrapped
    Stats stats = new Stats();

    @JsonAlias({"Profile"})
    @JsonUnwrapped
    Profile profile;

    public User(String name, String username, String password, Integer coins, Stats stats, Profile profile) {
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
}
