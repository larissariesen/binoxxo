package Binoxxo;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BinoxxoApp extends PApplet {
    private enum GameState {
        Running,
        Finished
    }
    private enum LevelState {
        Running,
        Finished,
    }
    LevelState levelState = LevelState.Running;
    GameState gameState = GameState.Running;

    int rect = 0;
    int cellSize;
    char[][] level; // [vertikal] [horziontal]
    String rules = "Es liegen 3 Gleiche nebeneinander \n" +
            " Zwei Spalten oder Zeilen sind gleich \n " +
            "Anzahl o und x stimmen nicht überrein";
    int currentLevel = 1;
    PImage trash;

    public static void main(String args[]) {
        PApplet.main(new String[]{BinoxxoApp.class.getName()});
    }

    public void settings() {
        size(100, 100);
    }

    public void setup() {
        surface.setLocation(100, 100);
        fill(0, 255, 0);
        cellSize = 50;
        trash = loadImage("data/bin.jpg");
    }

    public void draw() {
        if (rect == 0 || levelState == LevelState.Finished && gameState != GameState.Finished) {
            switch (currentLevel) {
                case 1 -> prepareLevelOne();
                case 2 -> prepareLevelTwo();
                case 3 -> prepareLevelThree();
            }
        }
        background(255);
        for (int i = 0; i < level.length; i++) {
            // Begin loop for rows
            for (int j = 0; j < level[i].length; j++) {
                // Scaling up to draw a rectangle at (x,y)
                int x = (cellSize * i) + 20;
                int y = (cellSize * j) + 20;
                stroke(0);
                fill(0);
                textSize(50);
                textAlign(CENTER, CENTER);
                text(level[i][j], x + 25, y + 15);
                // For every column and row, a rectangle is drawn at an (x,y) location.
                noFill();
                rect(x, y, cellSize, cellSize);
            }
        }
        textSize(15);
        textAlign(LEFT, TOP);
        // TODO - show mini Anleitung
        text(rules, 20, ((cellSize * rect) + 50));
    }

    public void mouseClicked(MouseEvent e) {
        if (levelState == LevelState.Running) {
            for (int i = 0; i < level.length; i++) {
                for (int j = 0; j < level[i].length; j++) {
                    if (e.getX() >= 20 + (i * 50) && e.getX() <= 20 + ((i + 1) * 50)
                            && e.getY() >= 20 + (j * 50) && e.getY() <= 20 + ((j + 1) * 50)) {
                        switch (level[i][j]) {
                            case 'x' -> level[i][j] = 'o';
                            case 'o' -> level[i][j] = ' ';
                            case ' ' -> level[i][j] = 'x';
                        }
                    }
                }
            }
        }
        if (e.getX() >= 40+(50*level.length) &&
                e.getX() <= 70+(50*level.length) &&
                e.getY() >= 30 && e.getY() <= 70){
            resetLevel();
        }
        checkIfFinished();
        redraw();
    }

    private void checkIfFinished() {
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[i].length; j++) {
                if (level[i][j] == ' ') {
                    rules = "";
                    return;
                }
            }
        }
        if (errorHandling().length() > 0)
            rules = errorHandling();
        else {
            levelState = LevelState.Finished;
            if(currentLevel < 3) {
                rules = "Geschafft, hier das nächste Level";
                currentLevel++;
            } else {
                rules = "Alle Levels geschafft :D";
                gameState = GameState.Finished;
            }
        }
    }

    private String errorHandling() {
        List<String> errorMessages = new ArrayList<>();
        if (checkForThree())
            errorMessages.add("Es liegen 3 Gleiche nebeneinander");
        if (checkForDuplicates())
            errorMessages.add("Zwei Spalten oder Zeilen sind gleich");
        if (checkForOddChars())
            errorMessages.add("Anzahl o und x stimmen nicht überrein");
        return errorMessages.stream().collect(Collectors.joining("\n"));
    }

    private boolean checkForThree() {
        String[] arrX = new String[level.length];
        String[] arrY = new String[level.length];

        for (int i = 0; i < level.length; i++) {
            String column = new String(level[i]).toLowerCase();

            StringBuilder rowBuilder = new StringBuilder();
            for (int j = 0; j < level[i].length; j++) {
                rowBuilder.append(level[j][i]);
            }
            String row = rowBuilder.toString().toLowerCase();
            arrY[i] = column;
            arrX[i] = row;
        }

        if (Arrays.stream(arrX).anyMatch(row -> row.contains("xxx") || row.contains("ooo"))) {
            return true;
        }
        if (Arrays.stream(arrY).anyMatch(column -> column.contains("xxx") || column.contains("ooo"))) {
            return true;
        }

        return false;
    }

    private boolean checkForDuplicates() {
        String[] arrX = new String[level.length];
        String[] arrY = new String[level.length];

        for (int i = 0; i < level.length; i++) {
            String column = new String(level[i]).toLowerCase();

            StringBuilder rowBuilder = new StringBuilder();
            for (int j = 0; j < level[i].length; j++) {
                rowBuilder.append(level[j][i]);
            }
            if (Arrays.stream(arrY).anyMatch(column::equals)) {
                return true;
            }
            String row = rowBuilder.toString().toLowerCase();
            if (Arrays.stream(arrX).anyMatch(row::equals)) {
                return true;
            }
            arrY[i] = column;
            arrX[i] = row;
        }
        return false;
    }

    private boolean checkForOddChars() {
        for (int i = 0; i < level.length; i++) {
            int counterXX, counterOX, counterXY, counterOY;
            counterXX = counterOX = counterXY = counterOY = 0;
            for (int j = 0; j < level[i].length; j++) {
                // Check for X in Spalte
                if (level[i][j] == 'x' || level[i][j] == 'X') {
                    counterXY++;
                    // Check for Y in Spalte
                } else if (level[i][j] == 'o' || level[i][j] == 'O') {
                    counterOY++;
                }
                // Check for X in Zeile
                if (level[j][i] == 'x' || level[j][i] == 'X') {
                    counterXX++;
                    // Check for Y in Zeile
                } else if (level[j][i] == 'o' || level[j][i] == 'O') {
                    counterOX++;
                }
            }
            if (counterXY != counterOY || counterXX != counterOX) {
                return true;
            }
        }
        return false;
    }

    private void prepareLevelOne() {
        surface.setSize(300, 350);
        rect = 4;
        level = new char[rect][rect];
        fillArrayEmpty();
        level[1][0] = 'X';
        level[3][0] = 'O';
        level[2][1] = 'O';
        level[1][2] = 'O';
        level[0][3] = 'X';
        level[1][3] = 'X';
        level[3][3] = 'O';
        levelState = LevelState.Running;
    }

    private void prepareLevelTwo() {
        surface.setSize(400, 450);
        rect = 6;
        level = new char[rect][rect];
        fillArrayEmpty();
        level[0][1] = 'X';
        level[0][3] = 'O';
        level[1][4] = 'O';
        level[1][5] = 'O';
        level[3][2] = 'X';
        level[3][3] = 'X';
        level[4][5] = 'X';
        level[5][1] = 'X';
        levelState = LevelState.Running;
    }

    private void prepareLevelThree() {
        surface.setSize(500, 550);
        rect = 10;
        level = new char[rect][rect];
        fillArrayEmpty();
        level[0][0] = 'X';
        level[0][1] = 'O';
        level[0][6] = 'O';
        level[1][0] = 'X';
        level[1][2] = 'O';
        level[1][4] = 'X';
        level[1][8] = 'X';
        level[2][0] = 'O';
        level[2][2] = 'O';
        level[3][1] = 'X';
        level[3][7] = 'X';
        level[3][8] = 'X';
        level[4][0] = 'X';
        level[4][3] = 'O';
        level[4][5] = 'X';
        level[5][1] = 'X';
        level[5][2] = 'O';
        level[5][4] = 'O';
        level[5][5] = 'X';
        level[5][7] = 'O';
        level[5][8] = 'X';
        level[6][1] = 'X';
        level[6][7] = 'O';
        level[7][0] = 'O';
        level[7][2] = 'X';
        level[7][6] = 'O';
        level[7][8] = 'X';
        level[7][9] = 'O';
        level[8][0] = 'O';
        level[8][2] = 'X';
        level[8][6] = 'O';
        level[8][8] = 'X';
        level[8][9] = 'O';
        level[9][3] = 'O';
        level[9][4] = 'X';
        level[9][6] = 'O';
        levelState = LevelState.Running;
    }

    private void fillArrayEmpty() {
        Arrays.stream(level).forEach(row -> Arrays.fill(row, ' '));
    }
}
