package game;

import app.models.*;
import app.service.PlayerServiceImpl;
import enums.Element;
import helper.Tuple;
import http.ContentType;
import http.HttpStatus;
import server.Response;

import java.util.*;

public class Arena {
    private static final Random rnd = new Random();
    private static final int MAX_ROUNDS = 100;
    PlayerServiceImpl playerService = new PlayerServiceImpl();
    // TODO: ADDING CARDS TO DECK THINK ABOU DUPLICATE CAN I ADD THE SAME CARD MORE THAN ONCE

    // TODO: CHECK IF DECK HAS 4 Cards in it
    public Response battle(User player1, User player2) {
        String winnerId = null;

        Optional<Deck> optionalDeckP1 = playerService.findDeckByUserId(player1.getId());
        Optional<Deck> optionalDeckP2 = playerService.findDeckByUserId(player2.getId());

        if (optionalDeckP1.isEmpty() || optionalDeckP2.isEmpty()) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Something went wrong\"}"
            );
        }
        Deck p1Deck = optionalDeckP1.get();
        Deck p2Deck = optionalDeckP2.get();

        ArrayList<Card> p1DeckCards = (ArrayList<Card>) playerService.findCardsByDeckId(p1Deck.getId());
        ArrayList<Card> p2DeckCards = (ArrayList<Card>) playerService.findCardsByDeckId(p2Deck.getId());

        if (p1DeckCards.size() != 4 || p2DeckCards.size() != 4) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Something went wrong\"}"
            );
        }

        for (int i = 0; i < MAX_ROUNDS; i++) {
            if (p1DeckCards.isEmpty()) {
                winnerId = player2.getId();
                updateScore(player2, player1);
                printWinner(player2);
                break;
            } else if (p2DeckCards.isEmpty()) {
                winnerId = player1.getId();
                updateScore(player1, player2);
                printWinner(player1);
                break;
            }
            Card cP1 = drawCard(p1DeckCards);
            Card cP2 = drawCard(p2DeckCards);

            // Create Copy of Cards
            Card player1CardCopy = new MonsterCard(cP1);
            Card player2CardCopy = new MonsterCard(cP2);

            System.out.println("CARD AFTER CLONE: " + player1CardCopy);
            System.out.println("CARD AFTER CLONE: " + player2CardCopy);

            System.out.print(player1.getUsername() + ": " + cP1.getName() + " (" + cP1.getDamage() + " Damage) vs ");
            System.out.print(player2.getUsername() + ": " + cP2.getName() + " (" + cP2.getDamage() + " Damage) \n");

            setSpecialities(cP1, cP2);
            System.out.println("Damage C1: " + cP1.getDamage() + " Damage C2: " + cP2.getDamage());
            if (cP1.getDamage() == cP2.getDamage()) {
                System.out.println("THIS ROUND IS A DRAW BOTH CARDS HAVE SAME DMG");
                p1DeckCards.remove(cP1);
                p2DeckCards.remove(cP2);

                p1DeckCards.add(player1CardCopy);
                p2DeckCards.add(player2CardCopy);
            } else if (cP1.getDamage() < cP2.getDamage()) {
                System.out.println("Player 2 Card is stronger");
                // Delete the card that is in the deck
                p1DeckCards.remove(cP1);
                p2DeckCards.add(player1CardCopy);

                p2DeckCards.remove(cP2);
                p2DeckCards.add(player2CardCopy);

                player1CardCopy.setUserId(player2.getId());
                player1CardCopy.setDeckId(p2Deck.getId());
                System.out.println("COPIED CARD: " + player1CardCopy);
                playerService.updateCard(player1CardCopy, player1CardCopy);
            } else {
                System.out.println("Player 1 Card is stronger");
                p2DeckCards.remove(cP2);
                p1DeckCards.add(player2CardCopy);

                p1DeckCards.remove(cP1);
                p1DeckCards.add(player1CardCopy);

                player2CardCopy.setUserId(player1.getId());
                player2CardCopy.setDeckId(p1Deck.getId());
                System.out.println("COPIED CARD: " + player2CardCopy);
                playerService.updateCard(player2CardCopy, player2CardCopy);
            }
        }

        updateDecks(p1DeckCards);
        updateDecks(p2DeckCards);

        if (winnerId == null) {
            player1.getStats().setDraws(player1.getStats().getDraws() + 1);
            player2.getStats().setDraws(player2.getStats().getDraws() + 1);
            playerService.updateUser(player1, player1);
            playerService.updateUser(player2, player2);
            System.out.println("THE GAME ENDED IN A DRAW");
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"The Game ended in a draw\"}"
            );
        }


        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"The User with the ID: " + winnerId + " won the game\"}"
        );
    }

    private void updateDecks(List<Card> cards) {
        cards.forEach(card -> {
            card.setDeckId(null);
            playerService.updateCard(card, card);
        });
    }

    private void setSpecialities(Card c1, Card c2) {
        // If card is a SpellCard call damageEffectiveness to and set new damage
        if (c1 instanceof SpellCard spellCard) {
            float dmg = spellCard.damageEffectiveness(c2.getElementType());
            c1.setDamage(dmg);
        } else if (c2 instanceof SpellCard spellCard) {
            float dmg = spellCard.damageEffectiveness(c1.getElementType());
            c2.setDamage(dmg);
        }

        // Set specialities using two tuples the KEY Tuple contains the STRENGTH
        // the VALUE Tuple contains the WEAKNESS against the STRENGTH
        // < STRENGTH -> WEAKNESS >
        HashMap<Tuple<String, Element>, Tuple<String, Element>> specialities = new HashMap<>();
        specialities.put(new Tuple<>("dragon", null), new Tuple<>("goblin", null));
        specialities.put(new Tuple<>("wizard", null), new Tuple<>("ork", null));
        specialities.put(new Tuple<>("spell", Element.WATER), new Tuple<>("knight", null));
        specialities.put(new Tuple<>("kraken", null), new Tuple<>("spell", null));
        specialities.put(new Tuple<>("elves", Element.FIRE), new Tuple<>("dragon", null));

        // Loop over all specialities and check if any apply
        specialities.forEach((key, value) -> considerSpecialties(c1, c2, key.x, key.y, value.x, value.y, false));
    }

    private void considerSpecialties(Card c1, Card c2, String strength, Element strengthElement, String weakness, Element weaknessElement, boolean isSecondCheck) {
        if ((weaknessElement == null || weaknessElement == c1.getElementType()) && c1.getName().toLowerCase(Locale.ROOT).contains(weakness) &&
                (strengthElement == null || strengthElement == c2.getElementType()) && c2.getName().toLowerCase(Locale.ROOT).contains(strength)) {
            System.out.println(c1.getName() + " loses against " + c2.getName());
            c1.setDamage(0);
        }

        // Checks if this is the second call if true returns; otherwise would end in an inf. loop
        if (isSecondCheck) return;
        // SAME CHECK ONLY VICE VERSA
        considerSpecialties(c2, c1, strength, strengthElement, weakness, weaknessElement, true);
    }


    private Card drawCard(ArrayList<Card> cards) {
        return cards.get(rnd.nextInt(cards.size()));
    }

    private void printWinner(User user) {
        System.out.println("USER: " + user.getUsername() + " WON!");
    }

    private void updateScore(User winner, User loser) {
        // TODO: WRITE SERVICE METHODS THAT DO THIS
        // Update game counts
        winner.getStats().setWins(winner.getStats().getWins() + 1);
        loser.getStats().setLosses(loser.getStats().getLosses() + 1);

        // Update elo
        winner.getStats().setElo(winner.getStats().getElo() + 3);
        loser.getStats().setElo(loser.getStats().getElo() - 5);

        playerService.updateUser(winner, winner);
        playerService.updateUser(loser, loser);
    }
}
