package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAlias({"Type"})
    private String cardType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAlias({"MinimumDamage"})
    private Integer minimumDamage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAlias({"Coins"})
    private Integer coins;
}
