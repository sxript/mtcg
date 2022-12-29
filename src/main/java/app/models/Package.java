package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Package {
    private static final int COST = 5;
    private static final int SIZE = 5;

    @JsonAlias({"Id"})
    private String id = UUID.randomUUID().toString();

    @JsonAlias({"Price"})
    private int price = COST;

}
