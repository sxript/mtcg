package app.models;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BattleLog {
    private String id;
    private String json;
    private String message;
}
