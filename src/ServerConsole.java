//// This file contains material supporting section 3.7 of the textbook:
//// "Object Oriented Software Engineering" and is issued under the open-source
//// license found at www.lloseng.com


import java.io.*;
import java.util.Scanner;

import common.*;

public class ServerConsole implements ChatIF {

    private final static int DEFAULT_PORT = 5555;

    private final EchoServer server;
    private final Scanner fromConsole;

    public ServerConsole(int port) {
        server = new EchoServer(port);
        fromConsole = new Scanner(System.in);

        try {
            server.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    @Override
    public void display(String message) {
        if (message.charAt(0) == '#') {//its a command
            try {
                command(message);
            } catch (IOException e) {
                System.out.println("Command error");
            }
        } else {
            System.out.println("SERVER MESSAGE> " + message);
            server.sendToAllClients("SERVER MESSAGE> " + message);
        }
    }

    public void command(String message) throws IOException {
        String mes[] = message.split(" ");//split the input into different pieces
        String command = mes[0];//the command is the first part of the input
        switch (command) {
            case "#quit":
                System.exit(0);
                break;
            case "#stop":
                server.sendToAllClients("WARNING - The server has stopped listening for connections.");
                server.stopListening();

                break;

            case "#close":
                System.out.println("WARNING - The server has closed.");
                server.sendToAllClients("WARNING - The server has closed.");
                server.close();

                break;
            case "#setport":
                try {
                    String port = mes[1].replace("<", "");
                    port = port.replace(">", "");
                    try {
                        server.setPort(Integer.parseInt(port));
                        System.out.println("Port set to: " + port);

                    } catch (NumberFormatException e) {
                        System.out.println("Port is not a number");
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Port is empty");
                }
                break;
            case "#start":
                if (server.isListening()) {
                    System.out.println("Error - the server is already running.");

                } else {
                    server.listen();
                }
                break;
            case "#getport":
                System.out.println("The port number is: " + server.getPort());
                break;
            default:
                System.out.println("Incorrect Command, you can try:");
                System.out.println("#quit");
                System.out.println("#stop");
                System.out.println("#close");
                System.out.println("#setport <portname> (or just portname)");
                System.out.println("#start");
                System.out.println("#getport");
        }
    }

    private void accept() {
        try {
            String message;
            while (true) {
                message = fromConsole.nextLine();
                display(message);
            }
        } catch (Exception ex) {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    public static void main(String[] args) {
        int port = 0;

        try {
            port = Integer.parseInt(args[0]); //get port number from command line

        } catch (Throwable t) {
            port = DEFAULT_PORT;
        }

        ServerConsole console = new ServerConsole(port);
        console.accept();
    }
}
