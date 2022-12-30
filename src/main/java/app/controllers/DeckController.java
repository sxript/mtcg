package app.controllers;

import app.dao.DeckDao;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


public class DeckController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private DeckDao deckDao;

    public DeckController (DeckDao deckDao) {
        setDeckDao(deckDao);
    }

}
