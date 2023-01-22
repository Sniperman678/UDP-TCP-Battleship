package com.company;

import com.company.common.Context;
import com.company.tcp.joingame.JoinGame;
import com.company.tcp.startgame.StartGame;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String []args) throws IOException {
        System.out.println("Welcome to the game");
        System.out.println("What would you like to do?");
        System.out.println("Enter 1 to start a new game \nEnter 2 to find a game");
        Scanner scanner = new Scanner(System.in);
        int decision = scanner.nextInt();

        if(decision == 1) {
            System.out.println("Starting new game.");
            Context context = new Context();
            StartGame startGame = new StartGame(context);
            startGame.start();

        } else if (decision == 2) {
            System.out.println("Finding a game for you");
            Context context = new Context();
            JoinGame joinGame = new JoinGame(context);
            joinGame.joinGame();
        }
    }
}
