package app.models;

import enums.Element;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class SpellCard extends Card {

    public SpellCard(String id, String name, float damage, Element elementType, String packageId, String userId, String deckId) {
        super(id , name, damage, elementType, packageId, userId, deckId);
    }

    public SpellCard(Card card) {
        this(card.getId(), card.getName(), card.getDamage(), card.getElementType(), card.getPackageId(), card.getUserId(), card.getDeckId());
    }

}
