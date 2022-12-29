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

    public SpellCard(String id, String name, float damage, Element elementType, String packageId, String userId) {
        super(id , name, damage, elementType, packageId, userId);
    }

    public float damageEffectiveness(Element opponentElementType) {
        if(this.getElementType() == opponentElementType) return getDamage();
        else if(this.getElementType() == Element.FIRE && opponentElementType == Element.NORMAL
            || this.getElementType() == Element.WATER && opponentElementType == Element.FIRE
            || this.getElementType() == Element.NORMAL && opponentElementType == Element.WATER) return 2 * getDamage();

        System.out.println("UNEFFECTIVE");
        return getDamage() / 2;
    }
}
