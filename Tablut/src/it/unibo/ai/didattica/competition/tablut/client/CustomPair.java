package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;

public class CustomPair {
    private Action key;
    private int value;

    public CustomPair(Action a, int score) {
        this.key = a;
        this.value = score;
    }

    public Action getKey() {
        return this.key;
    }

    public int getValue() {
        return value;
    }

    public void setKey(Action key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
