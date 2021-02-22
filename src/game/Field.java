package game;

import java.util.ArrayList;
import java.util.List;
import gui.GameBoard;

public class Field {

    private GameBoard board;
    private final int[][] field;
    private final int[][] state;
    private final int rows;
    private final int columns;
    private final int numvir;
    private int correctFlag = 0;
    private int Counter = 0;
    private int numUncovered = 0;

    public Field(GameBoard board, int rows, int columns, int virus) {
        this.board = board;
        this.rows = rows;
        this.columns = columns;
        this.numvir = virus;
        field = new int[rows][columns];
        state = new int[rows][columns];
    }
    private int getAvailablevirBlockCount(int row, int col) {           //gets the available number of blocks
        int availablevirBlockCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (isOutsidePerimeter(row, col, i, j)) {
                    availablevirBlockCount++;
                }
            }
        }
        return availablevirBlockCount;
    }

    public void generatefield(int row, int col) {                      // geennerates the game map
        int availablevirBlocks = getAvailablevirBlockCount(row, col);
        List<Integer> availablevirPosition = new ArrayList<>(availablevirBlocks);
        createAvailablePosArrayList(row, col, availablevirPosition);
        spawnVirus(availablevirBlocks, availablevirPosition);
    }

    private void createAvailablePosArrayList(int row, int col, List<Integer> availablevirBlockPosition) {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (isOutsidePerimeter(row, col, i, j)) {
                    availablevirBlockPosition.add(count);
                }
                count++;
            }
        }
    }
    public boolean isSanitized(int row, int col) {
        return state[row][col] == 2;
    }     //for amrking sanitizer

    public void unwash(int row, int col) {                  //for unmarking a tile
        state[row][col] = 0;
        if (field[row][col] == -1) {
            correctFlag--;
        }
        Counter--;
    }

    public void sanitizer(int row, int col) {
        state[row][col] = 2;
        if (field[row][col] == -1) {
            correctFlag++;
        }
        Counter++;
    }


    private void spawnVirus(int availablevirCount, List<Integer> availablevirPosition) {     //places viruses randomly
        for (int i = 0; i < numvir; i++) {
            int random = (int) (Math.random() * availablevirCount);
            int position = availablevirPosition.remove(random);
            int rRow = position / columns;
            int rCol = position % columns;
            field[rRow][rCol] = -1;
            availablevirCount--;
        }
    }

    private boolean isOutsidePerimeter(int row, int col, int i, int j) {
        return (i > row + 1 || i < row - 1) || (j > col + 1 || j < col - 1);
    }

    public int getAdjacentVirusCount(int row, int col) {
        return field[row][col];
    }

    private void calculateAdjacentVirusCount(int row, int col) {
        int adjacent = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (checkForVirus(row, col, i, j)) {
                    adjacent++;
                }
            }
        }
        field[row][col] = adjacent;
    }

    private boolean checkForVirus(int row, int col, int i, int j) {
        return isInsideBounds(row, col, i, j) && isVirus(row+i, col+j);
    }

    public int getCorrectFlagCounter() {
        return this.correctFlag;
    }

    public int getFlagCounter() {
        return this.Counter;
    }

    private void incrementUncovered() {
        numUncovered++;
    }

    public int getNumUncovered() {
        return this.numUncovered;
    }

    public int countAdjacentFlags(int row, int col) {
        int adjacentFlags = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isInsideBounds(row, col, i, j) && isSanitized(row+i, col+j)) {
                    adjacentFlags++;
                }
            }
        }
        return adjacentFlags;
    }
    private void uncover(int row, int col) {
        state[row][col] = 1;
        incrementUncovered();
    }
    public boolean isUncovered(int row, int col) {
        return state[row][col] == 1;
    }

    public boolean isInsideBounds(int row, int col, int i, int j) {
        return row + i >= 0 && row + i < this.rows && col + j >= 0 && col + j < this.columns;
    }


    public int findVirus(int row, int col) {                                                                          
        if (!isUncovered(row,col) && isVirus(row, col) && !isSanitized(row, col)) {
            return -1;
        } else if (!isUncovered(row, col) && !isSanitized(row, col)) {
            calculateAdjacentVirusCount(row, col);
            uncover(row, col);
            board.uncoverfieldBut(field[row][col], row, col);
            if (field[row][col] > 0) {
                return field[row][col];
            } else if (field[row][col] == 0) {
                findsurroundingVirus(row, col);
            }
            return 0;
        } else {
            return -2;
        }
    }

    private void findsurroundingVirus(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isInsideBounds(row, col, i, j) && !isUncovered(row + i, col + j)) {
                    findVirus(row + i, col + j);
                }
            }
        }
    }

    private boolean isVirus(int row, int col) {
        return field[row][col] == -1;
    }
}
