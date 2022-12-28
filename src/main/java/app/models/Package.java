package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@Getter
@Setter
public class Package {
    private static final int COST = 5;
    private static final int SIZE = 5;

    @JsonAlias({"Id"})
    private String id;

    @JsonAlias({"Price"})
    private int price = COST;

    public Package() {
        setId(UUID.randomUUID().toString());
    }

    public void setId(String id) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
    }
}
