package factories;

import app.models.Card;
import app.models.MonsterCard;
import app.models.SpellCard;
import enums.Type;

public class CardFactory implements AbstractFactory<Card, Type> {
    @Override
    public Card create(Type t) {
        if (Type.MONSTER == t) return new MonsterCard();
        return new SpellCard();
    }
}
