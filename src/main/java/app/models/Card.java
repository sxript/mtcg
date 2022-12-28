package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.Element;
import lombok.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MonsterCard.class, name = "monster"),
        @JsonSubTypes.Type(value = SpellCard.class, name = "spell")
})
@ToString
public abstract class Card {
    @JsonAlias({"Id"})
    private String id;

    @JsonAlias({"Name"})
    private String name;

    @JsonAlias({"Damage"})
    private float damage;

    @JsonAlias({"Element"})
    // TODO: NO DEFAULT ANYMORE
    private Element elementType = Element.NORMAL;

    @JsonAlias({"package_id"})
    private String packageId;

    protected Card(String id, String name, float damage, Element elementType, String packageId) {
        setId(id);
        setName(name);
        setDamage(damage);
        setElementType(elementType);
        setPackageId(packageId);
    }

    public void setName(String name) {
        Optional<Element> optionalElement = Arrays.stream(Element.values()).filter(element -> name.toUpperCase(Locale.ROOT).contains(element.name())).findAny();
        optionalElement.ifPresent(this::setElementType);
        this.name = name;
    }
}
