package Binoxxo;

import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.Arrays;

public class BinoxxoApp extends PApplet {
    // TODO Errorhandling checken
    // TODO verhalten wann Errors angezeigt werden
    // TODO Errors untereinander
    
    int rect;
    int cellSize;
    char[][] level; // [vertikal] [horziontal]
    char[][] arrayToCheck; // [vertikal] [horziontal]
    String info = " ";

    public static void main(String args[]) {
        PApplet.main(new String[]{BinoxxoApp.class.getName()});
    }

    public void settings() {
        size(900, 900);
    }

    public void setup() {
        fill(0, 255, 0);
        cellSize = 50;
        rect = 4;
        level = new char[rect][rect];
        arrayToCheck = new char[rect][rect];
        Arrays.stream(level).forEach(row -> Arrays.fill(row, ' '));
        prepareLevelOne();
    }

    public void draw() {
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
        textAlign(LEFT, BOTTOM);
        text(info, 20, ((cellSize * rect) + 50));
    }

    public void mouseClicked(MouseEvent e) {
        if (!info.contains("Geschafft")) {
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
        info = isFinished();
        redraw();
    }

    private void prepareLevelOne() {
        level[1][0] = 'X';
        level[3][0] = 'O';
        level[2][1] = 'O';
        level[1][2] = 'O';
        level[0][3] = 'X';
        level[1][3] = 'X';
        level[3][3] = 'O';
    }

    private void prepareLevelTwo() {
        level[0][1] = 'X';
        level[0][3] = 'O';
        level[1][4] = 'O';
        level[1][5] = 'O';
        level[3][2] = 'X';
        level[3][3] = 'X';
        level[4][5] = 'X';
        level[5][1] = 'X';
    }

    private void prepareLevelThree() {
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
    }

    private String isFinished() {
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[i].length; j++) {
                arrayToCheck[i][j] = level[i][j];
                if (level[i][j] == ' ') {
                    return " ";
                }
            }
        }
        return errorHandling();
    }

    private String errorHandling() {
        String info = "";
        info += checkForThree() + ", ";
        info += checkForDuplicates() + ", ";
        info += checkForOddChars();
        return info;
    }

    private String checkForOddChars() {
        int counterX = 0;
        int counterY = 0;
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[i].length; j++) {
                if (level[i][j] == 'x' || level[i][j] == 'X') {
                    counterX++;
                } else if (level[i][j] == 'o' || level[i][j] == 'O') {
                    counterY++;
                }
                // Funktioniert nur für quadratische Grids
                if (counterX < level[i].length || counterY < level[i].length) {
                    return "Anzahl o und x stimmen nicht überrein";
                }
                counterX = 0;
            }
        }
        return "";
    }

    private String checkForDuplicates() {
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[i].length; j++) {
                if (arrayToCheck[i] == arrayToCheck[i + j] ||
                        arrayToCheck[i][j] == arrayToCheck[i][j + i]) {
                    return "Zwei Spalten oder Zeilen sind gleich";
                }
            }
        }
        return "";
    }

    private String checkForThree() {
        for (int i = 0; i < level.length - 2; i++) {
            for (int j = 0; j < level[i].length - 2; j++) {
                if (
                        arrayToCheck[i][j] == arrayToCheck[i + 1][j] &&
                                arrayToCheck[i][j] == arrayToCheck[i + 2][j] ||
                                arrayToCheck[i][j] == arrayToCheck[i][j + 1] &&
                                        arrayToCheck[i][j] == arrayToCheck[i][j + 2]
                ) {
                    return "Es liegen 3 Gleiche nebeneinander";
                }
            }
        }
        return "";

    }
}
