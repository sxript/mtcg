package app.models;

import enums.Element;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class MonsterCard extends Card {
    public MonsterCard(String id, String name, float damage, Element elementType, String packageId, String userId) {
        super(id , name, damage, elementType, packageId, userId);
    }
}
