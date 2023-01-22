package com.company.tcp.startgame;

import com.company.broadcast.udp.UDPSender;
import com.company.common.Context;
import com.company.common.Message;
import com.company.game.Game;
import com.company.game.GameOperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class StartGame {
    private Context context;

    private Game game;

    public StartGame(Context context) {
        this.context = context;
    }

    public void start() {
        game = new Game();
        game.playerAGrid = GameOperations.createGrid(game.playerAGrid);

        Thread sender = new UDPSender(context);
        sender.start();
        tcpServer();
        sender.interrupt();
        System.out.println("GG");
    }

    public void tcpServer() {
        int defaultPortNumber = 5001;

        try (
                ServerSocket serverSocket = new ServerSocket(defaultPortNumber);
                Socket socket = serverSocket.accept();
        ) {
            while (!socket.isConnected()) {
                Thread.sleep(30 * 1000);
            }
            System.out.println("Game started");
            context.gameStart = true;

            Scanner scanner = new Scanner(System.in);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String sendingCommand = scanner.nextLine();  //Player 1 goes first
            out.println(sendingCommand);        //Sent to Player 2
            // Wait for response from player 2
            while (true) {
                String player2Command = in.readLine();
                if(player2Command.equals("END")) {
                    break;
                }
                System.out.println(player2Command);
                String command = player2Command.trim().split(":")[0];
                int row = -1;
                int col = -1;
                if("SUNK".equals(command)) {
                    row = player2Command.trim().split(":")[2].charAt(0) - 'A';
                    col = Integer.parseInt(player2Command.trim().split(":")[2].substring(1)) - 1;
                } else if("GAME OVER".equals(command)) {
                    out.println("END");
                } else {
                    row = player2Command.trim().split(":")[1].charAt(0) - 'A';
                    col = Integer.parseInt(player2Command.trim().split(":")[1].substring(1)) - 1;
                }

                if ("FIRE".equals(command)) {
                    Message message = new Message();
                    game.playerAGrid = GameOperations.bombGrid(game.playerAGrid, row, col, message);

                    if((message.message.trim().split(":")[0]).equals("SUNK") && (game.aliveShips-1 == 0 )) {
                        out.println("GAME OVER" + ":" + message.message + player2Command.trim().split(":")[1]);
                        break;
                    } else {
                        out.println(message.message + ":" + player2Command.trim().split(":")[1]);
                    }

                    System.out.println("Home Grid");
                    GameOperations.printGrid(game.playerAGrid);
                    System.out.println("Opposition Grid");
                    GameOperations.printGrid(game.playerBGrid);

                    System.out.println("Your turn(SAY FIRE:XY)");
                    String response = scanner.nextLine();
                    out.println(response);
                } else if ("MISS".equals(command) || "HIT".equals(command) || "SUNK".equals(command)) {
                    game.playerBGrid = GameOperations.markOppositionGrid(game.playerBGrid, row, col, command);
                    System.out.println("Home Grid");
                    GameOperations.printGrid(game.playerAGrid);
                    System.out.println("Opposition Grid");
                    GameOperations.printGrid(game.playerBGrid);
                    if("SUNK".equals(command))  game.aliveShips--;
                } else if ("GAME OVER".equals(command)) {
                    in.close();
                    out.close();
                    System.exit(0);
                }
            }
        } catch (Exception ex) {
            System.out.println("IUnable to create a server");
        }
    }
}