package com.company.game;

import com.company.common.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameOperations {

    public static int [][] createGrid(int [][]grid) {
        List<Integer> ships = new ArrayList<>();
        ships.add(5);
        ships.add(4);
        ships.add(3);
        ships.add(2);
        ships.add(1);

        // Setting Random State for Ships
        Random random = new Random();// Setting Random State for Ships
        for (int ship : ships) {
            boolean trying = true;
            while (trying) {
                boolean isHorizontal = random.nextBoolean();
                int row = random.nextInt(10);
                int col = random.nextInt(10); //Setting Horizontal state for ships and if true continue if false try again and repeat if not trys vertical that if true continues if not repeats
                int count = 0;
                if (isHorizontal) {
                    for(int i = col; i < col + ship && i < 10; i++) {
                        if(grid[row][i] != 0) {
                            break;
                        }
                        count++;
                    }
                    if(count == ship) {
                        trying = false;
                        for (int i = col; i < col + ship; i++) {
                            grid[row][i] = ship;
                        }
                    }
                } else {
                    for(int i = row; i < row + ship && i <10; i++) {
                        if(grid[i][col] != 0) {
                            break;
                        }
                        count++;
                    }
                    if(count == ship) {
                        for (int i = row; i < row + ship; i++) {
                            grid[i][col] = ship;
                        }
                        trying = false;
                    }
                }
            }
        }

        return grid;
    }

    public static void printGrid(int [][]playerGrid) {
        char start = 'A';
        System.out.print("\t");
        for (int i = 0; i < 10; i++) {
            System.out.print(i+1 + "\t");
        }
        System.out.println();
        for(int i = 0; i < 10; i++ ) {
            System.out.print(start++ + "\t");
            for(int j = 0; j < 10; j++ ) {
                if(playerGrid[i][j] == 1000) {
                    System.out.print("H" + "\t");
                } else if (playerGrid[i][j] == 5000) {
                    System.out.print("M" + "\t");
                } else if (playerGrid[i][j] == 0) {
                    System.out.print("." + "\t");
                } else {
                    System.out.print(playerGrid[i][j] + "\t");
                }
            }
            System.out.println();
        }
    }

    public static int [][] bombGrid(int [][]playerGrid, int row, int col, Message message) {

        if(playerGrid[row][col] == 0) {
            playerGrid[row][col] = 5000;            //bombed empty place is represented by 1000
            message.message = "MISS";
        } else if(playerGrid[row][col] == 5000) {
            message.message = "Bombed the same place again";
        } else {
            int shipSize = playerGrid[row][col];
            playerGrid[row][col] = -playerGrid[row][col];
            int count = 0;
             for(int i = 0; i < 10; i++) {
                for(int j = 0; j < 10; j++ ) {
                    if(playerGrid[i][j] == shipSize) {
                        count++;
                    }
                }
             }
             if(count == 0) {
                 //This ship has sunk
                message.message = "SUNK:" + sizeToShipName(shipSize);

             } else {
                 message.message = "HIT";
             }
        }
        return playerGrid;
    }

    public static int[][] markOppositionGrid(int [][]grid, int row, int col, String hitOrMiss) {
        if("MISS".equals(hitOrMiss)) {
            grid[row][col] = 5000;
        } else {
            //SUNK and HIT
            grid[row][col] = 1000;
        }
        return grid;
    }

    private static String sizeToShipName(int size) {
        switch(size) {
            case 1:
                return "Submarine";
            case 2:
                return "Patrol Boat";
            case 3:
                return "Cruiser";
            case 4:
                return "Battleship";
            case 5:
                return "Aircraft carrier";
        }
        return "No idea";
    }
}
