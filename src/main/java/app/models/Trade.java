package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import enums.CardType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Trade {
    @JsonAlias({"Id"})
    private String id;

    @JsonAlias({"card_id", "CardToTrade"})
    private String cardId;

    @JsonAlias({"Type"})
    private String cardType;

    @JsonAlias({"MinimumDamage"})
    private int minimumDamage;
}
