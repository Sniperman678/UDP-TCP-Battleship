package com.company.tcp.joingame;

import com.company.broadcast.udp.UDPClient;
import com.company.common.Context;
import com.company.common.Message;
import com.company.game.Game;
import com.company.game.GameOperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class JoinGame {

    private Context context;

    private Game game;

    public JoinGame(Context context) {
        this.context = context;
        game = new Game();
    }

    public void joinGame() throws IOException {
        UDPClient client = new UDPClient(context);
        client.start();
        synchronized (context.lock) {
            while (!context.gameStart) {
                try {
                    context.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        game.playerBGrid = GameOperations.createGrid(game.playerBGrid);
        System.out.println("Joining the game now");
        join(context.hostIP, context.portNumber);
        System.out.println("GG");
    }

    public void join(String hostAddress, int portNumber) throws IOException {

        try (
                Socket echoSocket = new Socket(hostAddress, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);//Writing or Send to Server
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));//Reading from Server
                Scanner scanner = new Scanner(System.in);
        ) {
            while (true) {
                String player1Command = in.readLine();
                if("END".equals(player1Command) || player1Command == null) {
                    break;
                }
                System.out.println(player1Command);
                String command = player1Command.trim().split(":")[0];
                int row = -1;
                int col = -1;
                if("SUNK".equals(command)) {
                     row = player1Command.trim().split(":")[2].charAt(0) - 'A';
                     col = Integer.parseInt(player1Command.trim().split(":")[2].substring(1)) - 1;
                } else if("GAME OVER".equals(command)) {
                    out.print("END");
                } else {
                    row = player1Command.trim().split(":")[1].charAt(0) - 'A';
                    col = Integer.parseInt(player1Command.trim().split(":")[1].substring(1)) - 1;
                }

                if ("FIRE".equals(command)) {
                    Message message = new Message();
                    game.playerBGrid = GameOperations.bombGrid(game.playerBGrid, row, col, message);

                    if((message.message.trim().split(":")[0]).equals("SUNK") && (game.aliveShips-1 == 0 )) {
                        out.println("GAME OVER" + ":" + message.message + player1Command.trim().split(":")[1]);
                        break;
                    } else {
                        out.println(message.message + ":" + player1Command.trim().split(":")[1]);
                    }

                    System.out.println("Home Grid");
                    GameOperations.printGrid(game.playerBGrid);
                    System.out.println("Opposition Grid");
                    GameOperations.printGrid(game.playerAGrid);

                    System.out.println("Your turn(SAY FIRE:XY)");
                    String response = scanner.nextLine();
                    out.println(response);
                } else if ("MISS".equals(command) || "HIT".equals(command) || "SUNK".equals(command)) {
                    game.playerAGrid = GameOperations.markOppositionGrid(game.playerAGrid, row, col, command);

                    System.out.println("Home Grid");
                    GameOperations.printGrid(game.playerBGrid);
                    System.out.println("Opposition Grid");
                    GameOperations.printGrid(game.playerAGrid);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Can't find the host " + hostAddress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostAddress);
            System.exit(1);
        }
    }

}