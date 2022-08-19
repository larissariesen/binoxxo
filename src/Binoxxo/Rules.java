package Binoxxo;

import java.util.Arrays;

public enum Rules {
    THREE("Es dürfen nie 3 'x' oder 'o' nebeneinander liegen"),
    EVEN("Jede Spalte und Reihe enthält die gleiche Anzahl 'x' und 'o'"),
    IDENTICAL("Es dürfen nie zwei Spalten oder Reihen identisch sein");

    public final String rule;
    public boolean state;

    Rules(final String rule) {
        this.rule = rule;
        this.state = false;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public static boolean noneActive() {
        return Arrays.stream(Rules.values()).noneMatch(r -> r.state);
    }

    public static void resetStates() {
        Arrays.stream(Rules.values()).forEach(r -> r.state = false);
    }

}
