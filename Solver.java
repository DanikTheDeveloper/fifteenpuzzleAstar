package fifteenpuzzle;

import java.io.*;
import java.util.*;

public class Solver {
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;
    private int size;
    private State board, goal;
    /**
     * @param fileName
     * @throws FileNotFoundException if file not found
     * @throws BadBoardException     if the board is incorrectly formatted Reads a
     *                               board from file and creates the board
     */
    public void setSize(String fileName) throws IOException, BadBoardException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        int count = br.read();
        int s = br.read();
        if (s < '0' || s > '9')
            count = count - '0';
        else
            count = 10*(count - '0') + (s - '0');
        size = count;
    }
    public Solver(String fileName) throws IOException, BadBoardException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        setSize(fileName);
        int x = 0,y = 0;
        int[][] b = new int[size][size];
        int c1, c2, s;
        br.read();
        if(size > 9)
            br.read();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                s = br.read();
                c1 = br.read();
                c2 = br.read();
                if (s != ' ' && s != '\n') {
                    br.close();
                    throw new BadBoardException("error in line " + i);
                }
                if (c1 == ' ')
                    c1 = '0';
                if (c2 == ' ')
                    c2 = '0';
                b[i][j] = 10 * (c1 - '0') + (c2 - '0');
                if(b[i][j] == 0){
                    x = i;
                    y = j;
                }
            }
        }
        int count = 1;
        int[][] g = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                g[i][j] = count++;
        g[size-1][size-1] = 0;
        br.close();
        board = new State(b,size,x,y,0,null,5, 0,0);
        goal = new State(g,size,size-1,size-1,0,null,5, 0, 0);
    }

    /**
     *These three functions are just for testing
     */
    public static void solve(int num) throws BadBoardException, IOException, IllegalMoveException {
        String s = Integer.toString(num);
        if(num < 10)
            s = "0" + s;
        Solver game = new Solver(System.getProperty("user.dir") + "\\" + "src" + "\\" + "testcases" + "\\" + "board" + s +".txt");
        long startTime = System.nanoTime();
        Stack<Integer> moves = game.solution();
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000000;
        System.out.println("Board" + s + " took " + elapsedTime + " seconds and " + game.goal.getNumMoves()
                + " moves" + ". The steps are: ");
        String movesStr = "";
        while(!moves.isEmpty()) {
            switch (moves.pop()) {
                case UP: {
                    movesStr = movesStr + "->UP";
                    break;
                }
                case DOWN: {
                    movesStr = movesStr + "->DOWN";
                    break;
                }
                case RIGHT: {
                    movesStr = movesStr + "->RIGHT";
                    break;
                }
                case LEFT: {
                    movesStr = movesStr + "->LEFT";
                    break;
                }
                default: {
                    movesStr = movesStr + "";
                    break;
                }
            }
        }
        movesStr = movesStr.substring(2);
        System.out.println(movesStr + "\n");
        System.out.println("This is the visualization: ");
        System.out.println(game.board.toString());
        visualizer(game.goal);
    }
    public static void visualizer(State state) throws BadBoardException, IOException, IllegalMoveException {
        if(state.getPrev() == null)
            return;
        visualizer(state.getPrev());
        System.out.println("     |" + "\n" + "     V" + "\n"  + state.toString());
    }
    public Stack<Integer> solution() throws IllegalMoveException {
        HashTable table = new HashTable();
        PriorityQueue<State> pq = new PriorityQueue<State>();
        pq.offer(board);
        table.offer(board);
        while (!pq.isEmpty()) {
            State curr = pq.poll();
            if(size-curr.getLayer()>3 && curr.getH() == 0) {
                /*for(int i = 0; i < 1000;i++)
                    System.out.println("gay alarm"+"\n");
                System.out.println(curr.toString()+"\n");*/
                curr.nextLayer();
                table = new HashTable();
                pq = new PriorityQueue<State>();
                pq.offer(curr);
                table.offer(curr);
            }
            else if(size-curr.getLayer()<=3 && curr.getH() == 0) {
                goal = curr;
                return curr.getPath();
            }
            table.offer(curr);
            for (State next : curr.neighbours())
                if (table.offer(next))
                    pq.offer(next);
        }
        return null;
    }
    /**
     *These two functions are for printing the actual answer
     */
    public static void ans(int num) throws BadBoardException, IOException, IllegalMoveException {
        String s = Integer.toString(num);
        if(num < 10)
            s = "0" + s;
        Solver game = new Solver(System.getProperty("user.dir") + "\\" + "src" + "\\" + "testcases" + "\\" + "board" + s + ".txt");
        Stack<Integer> moves = game.solutionAns();
        String fileName = System.getProperty("user.dir") + "\\" + "src" + "\\" + "solutions" + "\\" + "sol" + s + ".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        while(!moves.isEmpty()) {
            writer.write(moves.pop() + " ");
            switch (moves.pop()) {
                case UP: {
                    writer.write("U");
                    break;
                }
                case DOWN: {
                    writer.write("D");
                    break;
                }
                case RIGHT: {
                    writer.write("R");
                    break;
                }
                case LEFT: {
                    writer.write("L");
                    break;
                }
                default: {
                    writer.write("");
                    break;
                }
            }
            writer.newLine();
        }
        writer.close();
    }
    public Stack<Integer> solutionAns() throws IllegalMoveException {
        HashTable table = new HashTable();
        PriorityQueue<State> pq = new PriorityQueue<State>();
        pq.offer(board);
        table.offer(board);
        while (!pq.isEmpty()) {
            State curr = pq.poll();
            if(size-curr.getLayer()>3 && curr.getH() == 0) {
                /*for(int i = 0; i < 1000;i++)
                    System.out.println("gay alarm"+"\n");
                System.out.println(curr.toString()+"\n");*/
                curr.nextLayer();
                table = new HashTable();
                pq = new PriorityQueue<State>();
                pq.offer(curr);
                table.offer(curr);
            }
            else if(size-curr.getLayer()<=3 && curr.getH() == 0) {
                goal = curr;
                return curr.getFullPath();
            }
            table.offer(curr);
            for (State next : curr.neighbours())
                if (table.offer(next))
                    pq.offer(next);
        }
        return null;
    }
    public static void main(String[] args) throws BadBoardException, IOException, IllegalMoveException {
        System.out.println("current dir: " + System.getProperty("user.dir") + "\n");
        for(int i = 1; i < 40;i++)
            if(i != 10)
                solve(i);
        /*for(int i = 10;i < 40;)
            ans(++i);*/
    }
}