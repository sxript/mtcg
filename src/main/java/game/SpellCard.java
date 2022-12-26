package game;

import game.Card;
import game.Element;

public class SpellCard extends Card {

    public SpellCard(String name, int damage, Element elementType) {
        super(name, damage, elementType);
    }

    public int damageEffectiveness(Element opponentElementType) {
        if(this.getElementType() == opponentElementType) return getDamage();
        else if(this.getElementType() == Element.FIRE && opponentElementType == Element.NORMAL
            || this.getElementType() == Element.WATER && opponentElementType == Element.FIRE
            || this.getElementType() == Element.NORMAL && opponentElementType == Element.WATER) return 2 * getDamage();

        System.out.println("UNEFFECTIVE");
        return getDamage() / 2;
    }
}
