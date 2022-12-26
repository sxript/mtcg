package game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
abstract class Card {
    private String name;
    private int damage;
    private Element elementType;

    public Card(String name, int damage, Element elementType) {
        setName(name);
        setDamage(damage);
        setElementType(elementType);
    }
}
