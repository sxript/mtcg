package factories;

import app.models.Card;
import app.models.MonsterCard;
import app.models.SpellCard;
import enums.CardType;

public class CardFactory implements AbstractFactory<Card, CardType> {
    @Override
    public Card create(CardType t) {
        if (CardType.MONSTER == t) return new MonsterCard();
        return new SpellCard();
    }
}
