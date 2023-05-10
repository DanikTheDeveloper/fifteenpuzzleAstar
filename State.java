package fifteenpuzzle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

class State implements Comparable<State>  {
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;
    private int[][] board;
    private int emptyRow, emptyCol, numMoves, size, lastMove, lastTile, layer, h;
    private State prev;
    public int getNumMoves(){return numMoves;}
    public int[][] getBoard(){return board;}
    public int getLastMove(){return lastMove;}
    public State getPrev(){return prev;}
    public int getR(){return emptyRow;}
    public int getC(){return emptyCol;}
    public int getLayer(){return layer;}

    public int getH(){return h;}
    public void nextLayer(){
        layer += 1;
        if(size-layer > 3)
            this.h = this.layerHeuristic();
        else
            this.h = this.heuristic();
    }
    public State(int[][] board, int size, int emptyRow, int emptyCol, int numMoves, State prev, int lastMove, int lastTile, int layer) {
        this.board = board;
        this.emptyRow = emptyRow;
        this.emptyCol = emptyCol;
        this.numMoves = numMoves;
        this.prev = prev;
        this.size = size;
        this.lastMove = lastMove;
        this.lastTile = lastTile;
        this.layer = layer;
        if(size-layer > 3)
            this.h = this.layerHeuristic();
        else
            this.h = this.heuristic();
    }
    public int layerHeuristic() {
        h = layerManhattan() + layerLinearConflict();
        return h;
    }

    private int layerManhattan() {
        int manhattan = 0;
        int correct = 1;
        int num;
        for (int i = 0; i < size; i++){
            num = board[i][layer];
            if (num != 0 && num != correct)
                manhattan += Math.abs(i - ((num - 1) / size)) + Math.abs(layer - ((num - 1) % size));
            correct++;
            num = board[layer][i];
            if (num != 0 && num != correct)
                manhattan += Math.abs(layer - ((num - 1) / size)) + Math.abs(i - ((num - 1) % size));
            correct++;
        }
        return manhattan;
    }

    private int layerLinearConflict() {
        int linearConflict = 0;
        int[][] rows = new int[size][size];
        int[][] columns = new int[size][size];
        for (int i = 0; i < size; i++) {
            if (board[i][layer] != 0) {
                rows[i][layer] = (board[i][layer] - 1) / size;
                columns[i][layer] = (board[i][layer] - 1) % size;
            }
            else {
                rows[i][layer] = -1;
                columns[i][layer] = -1;
            }
            if (board[layer][i] != 0) {
                rows[layer][i] = (board[layer][i] - 1) / size;
                columns[layer][i] = (board[layer][i] - 1) % size;
            }
            else {
                rows[layer][i] = -1;
                columns[layer][i] = -1;
            }
        }
        for (int i = 0; i < size; i++) {
            if (rows[i][layer] == i)
                for (int l = layer + 1; l < size; l++)
                    if (rows[i][l] == i && board[i][layer] > board[i][l])
                        linearConflict += 2;
            if (columns[i][layer] == layer)
                for (int l = i + 1; l < size; l++)
                    if (columns[l][layer] == layer && board[i][layer] > board[l][layer])
                        linearConflict += 2;
            if (rows[layer][i] == layer)
                for (int l = i + 1; l < size; l++)
                    if (rows[layer][l] == layer && board[layer][i] > board[layer][l])
                        linearConflict += 2;
            if (columns[layer][i] == i)
                for (int l = layer + 1; l < size; l++)
                    if (columns[l][i] == i && board[layer][i] > board[l][i])
                        linearConflict += 2;
        }
        return linearConflict;
    }
    public int heuristic() {
        h = manhattan() + linearConflict();
        return h;
    }

    private int manhattan() {
        int manhattan = 0;
        int correct = 1;
        int num;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                num = board[i][j];
                if (num != 0 && num != correct)
                    manhattan += Math.abs(i - ((num - 1) / size)) + Math.abs(j - ((num - 1) % size));
                correct++;
            }
        return manhattan;
    }

    private int linearConflict() {
        int linearConflict = 0;
        int[][] rows = new int[size][size];
        int[][] columns = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (board[i][j] != 0) {
                    rows[i][j] = (board[i][j] - 1) / size;
                    columns[i][j] = (board[i][j] - 1) % size;
                }
                else {
                    rows[i][j] = -1;
                    columns[i][j] = -1;
                }
            }
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (rows[i][j] == i)
                    for (int l = j + 1; l < size; l++)
                        if (rows[i][l] == i && board[i][j] > board[i][l])
                            linearConflict += 2;
                if (columns[i][j] == j)
                    for (int l = i + 1; l < size; l++)
                        if (columns[l][j] == j && board[i][j] > board[l][j])
                            linearConflict += 2;
            }
        return linearConflict;
    }
    public boolean isEqual(State goal) {
        for (int i = 0; i < size;i++)
            for (int j = 0; j < size; j++)
                if (board[i][j] != goal.getBoard()[i][j])
                    return false;
        return true;
    }
    public boolean isGoal(State goal) {
        for (int i = 0;i < size;i++)
            if (board[i][getLayer()] != goal.getBoard()[i][getLayer()])
                return false;
        for (int j = 0;j < size;j++)
            if (board[getLayer()][j] != goal.getBoard()[getLayer()][j])
                return false;
        return true;
    }
    public int[][] makeMove(int direction) throws IllegalMoveException {
        int[][] s = this.cloneBoard();
        switch (direction) {
            case UP: {
                s[emptyRow][emptyCol] = board[emptyRow + 1][emptyCol];
                s[emptyRow + 1][emptyCol] = 0;
                break;
            }
            case DOWN: {
                s[emptyRow][emptyCol] = board[emptyRow - 1][emptyCol];
                s[emptyRow - 1][emptyCol] = 0;
                break;
            }
            case RIGHT: {
                s[emptyRow][emptyCol] = board[emptyRow][emptyCol - 1];
                s[emptyRow][emptyCol - 1] = 0;
                break;
            }
            case LEFT: {
                s[emptyRow][emptyCol] = board[emptyRow][emptyCol + 1];
                s[emptyRow][emptyCol + 1] = 0;
                break;
            }
            default:
                throw new IllegalMoveException("Unexpected direction: " + direction);
        }
        return s;
    }
    public Iterable<State> neighbours() throws IllegalMoveException {
        List<State> neighbors = new ArrayList<>();
        if (emptyRow < size - 1)
            neighbors.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow+1][emptyCol], layer));
        if (emptyRow > 0)
            neighbors.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow-1][emptyCol], layer));
        if (emptyCol > 0)
            neighbors.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol-1], layer));
        if (emptyCol < size - 1)
            neighbors.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol+1], layer));
        return neighbors;
    }
    private String num2str(int i) {
        if (i == 0)
            return "  ";
        else if (i < 10)
            return " " + Integer.toString(i);
        else
            return Integer.toString(i);
    }
    public String toString() {
        String ans = "";
        for (int i = 0; i < size; i++) {
            ans += num2str(board[i][0]);
            for (int j = 1; j < size; j++)
                ans += " " + num2str(board[i][j]);
            ans += "\n";
        }
        ans = ans.substring(0, ans.length() - 1);
        return ans;
    }
    public int[][] cloneBoard() {
        int[][] newBoard = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newBoard[i][j] = board[i][j];
        return newBoard;
    }
    public Integer hashCodeBoard() {
        int code = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                code = code * 31 + board[i][j];
        return code/1000 + this.hashCode();
    }
    public Stack<Integer> getPath(){
        Stack<Integer> path = new Stack<Integer>();
        State current = this;
        while(current.prev != null) {
            path.push(current.lastMove);
            current = current.getPrev();
        }
        return path;
    }
    public Stack<Integer> getFullPath(){
        Stack<Integer> path = new Stack<Integer>();
        State current = this;
        while(current.prev != null) {
            path.push(current.lastMove);
            path.push(current.lastTile);
            current = current.getPrev();
        }
        return path;
    }
    @Override
    public int compareTo(State state) {
        if(this.numMoves + this.h > state.numMoves + state.h)
            return 1;
        else if(this.numMoves + this.h < state.numMoves + state.h)
            return -1;
        return 0;
    }
}
