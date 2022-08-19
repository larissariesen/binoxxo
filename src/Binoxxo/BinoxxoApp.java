package Binoxxo;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.Arrays;

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

    int spaceLeft = 20;
    int yAxisTrash = 75;
    int yAxisGrid = 70;
    int rect = 0;
    int cellSize = 50;
    char[][] level; // [vertikal] [horziontal]
    int currentLevel = 1;
    String opManual = "1x Click = X -- 2x Click = O -- 3x Click = leeren";
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
        textAlign(LEFT, TOP);
        textSize(30);
        fill(0);
        text("BinoXXo - Level " + currentLevel, 15, 5);
        textSize(15);
        text(opManual, spaceLeft, 40);
        image(trash, 40 + (cellSize * level.length), yAxisTrash, 30, 30);
        for (int i = 0; i < level.length; i++) {
            // Begin loop for rows
            for (int j = 0; j < level[i].length; j++) {
                // Scaling up to draw a rectangle at (x,y)
                int x = (cellSize * i) + spaceLeft;
                int y = (cellSize * j) + yAxisGrid;
                stroke(0);
                textSize(50);
                textAlign(CENTER, CENTER);
                text(level[i][j], x + 25, y + 15);
                // For every column and row, a rectangle is drawn at an (x,y) location.
                noFill();
                rect(x, y, cellSize, cellSize);
            }
        }
        Rules[] rules = Rules.values();
        textSize(15);
        textAlign(LEFT, TOP);
        for (int i = 0; i < rules.length; i++) {
            float colorR = 0;
            if (rules[i].getState())
                colorR = 255;
            fill(colorR, 0, 0);
            text(rules[i].rule, spaceLeft, (80 + (50 * level.length) + (i * 20)));
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (levelState == LevelState.Running) {
            for (int i = 0; i < level.length; i++) {
                for (int j = 0; j < level[i].length; j++) {
                    if (e.getX() >= spaceLeft + (i * cellSize) && e.getX() <= spaceLeft + ((i + 1) * cellSize)
                            && e.getY() >= yAxisGrid + (j * cellSize) && e.getY() <= yAxisGrid + ((j + 1) * cellSize)) {
                        switch (level[i][j]) {
                            case 'x' -> level[i][j] = 'o';
                            case 'o' -> level[i][j] = ' ';
                            case ' ' -> level[i][j] = 'x';
                        }
                    }
                }
            }
        }
        if (e.getX() >= 40 + (cellSize * level.length) &&
                e.getX() <= 70 + (cellSize * level.length) &&
                e.getY() >= yAxisTrash && e.getY() <= 105) {
            resetLevel();
        }
        checkIfFinished();
        redraw();
    }

    private void resetLevel() {
        Rules.resetStates();
        switch (currentLevel) {
            case 1 -> prepareLevelOne();
            case 2 -> prepareLevelTwo();
            case 3 -> prepareLevelThree();
        }
    }

    private void checkIfFinished() {
        for (char[] column : level) {
            for (char row : column) {
                if (row == ' ') {
                    Rules.resetStates();
                    return;
                }
            }
        }
        errorHandling();
        if (Rules.noneActive()) {
            levelState = LevelState.Finished;
            Rules.resetStates();
            if (currentLevel < 3) {
                currentLevel++;
            } else {
                gameState = GameState.Finished;
            }
        }

    }

    private void errorHandling() {
        Rules.THREE.setState(checkForThree());
        Rules.IDENTICAL.setState(checkForDuplicates());
        Rules.EVEN.setState(checkForOddChars());
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
            if (Arrays.asList(arrY).contains(column)) {
                return true;
            }
            String row = rowBuilder.toString().toLowerCase();
            if (Arrays.asList(arrX).contains(row)) {
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
        surface.setSize(400, 400);
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
        surface.setSize(450, 500);
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
        surface.setSize(550, 600);
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
        level[8][1] = 'X';
        level[8][7] = 'X';
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
