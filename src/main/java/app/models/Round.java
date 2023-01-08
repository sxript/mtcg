package app.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Round {
    private final int round;
    private final List<String> messages = new ArrayList<>();

    public Round (int round) {
        this.round = round;
    }
}
