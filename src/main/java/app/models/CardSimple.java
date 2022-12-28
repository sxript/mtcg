package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import enums.Element;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class CardSimple {
    @JsonAlias({"Id"})
    private String id;

    @JsonAlias({"Name"})
    private String name;

    @JsonAlias({"Damage"})
    private int damage;

    @JsonAlias({"type"})
    private String type;

    public CardSimple(String id, String name, int damage, String type) {
        setId(id);
        setName(name);
        setDamage(damage);
        setType(type);
    }

}
