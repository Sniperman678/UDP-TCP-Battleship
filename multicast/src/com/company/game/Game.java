package com.company.game;

public class Game {
    public int [][] playerAGrid;
    public int [][] playerBGrid;

    public int aliveShips = 5;

    public Game() {
        this.playerAGrid = new int[10][10];
        this.playerBGrid = new int[10][10];
    }
}
