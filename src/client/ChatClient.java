// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;

import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;
    String id;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String id, String host, int port, ChatIF clientUI)
            throws IOException {

        super(host, port); //Call the superclass constructor

        this.clientUI = clientUI;
        this.id = id;
        openConnection();

        sendToServer("#login <" + id + ">");

    }


    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        clientUI.display(msg.toString());
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {
        if (message.charAt(0) == '#') {//when the message is a command
            try {
                command(message);
            } catch (IOException e) {
            }
        } else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display
                        ("Could not send message to server.  Terminating client.");
                quit();
            }
        }
    }

    private void command(String message) throws IOException {
        String mes[] = message.split(" ");//split the input into different pieces
        String command = mes[0];//the command is the first part of the input
        switch (command) {
            case "#quit":
                sendToServer(command);//
                quit();
                break;
            case "#logoff":
                sendToServer(command);//
              closeConnection();

              break;
            case "#sethost":
                if (isConnected()) {
                    System.out.println("Can't change host while logged in");
                } else {
                    String host = mes[1].replace("<", "");
                    host = host.replace(">", "");

                    setHost(host);
                    System.out.println("Host set to: " + host);

                }
                break;
            case "#setport":

                try {
                    String port = mes[1].replace("<", "");
                    port = port.replace(">", "");
//                  int portNum=Integer.parseInt(port);
                    try {
                        setPort(Integer.parseInt(port));
                      System.out.println("Port set to: " + port);

                    } catch (NumberFormatException e) {
                        System.out.println("Port is not a number");
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Port not specified");
                }

                break;
            case "#login":
                if (isConnected()) {
                    System.out.println("you connected to server already.");

                } else {
                    openConnection();
                    sendToServer("#login <" + id + ">");
                }
                break;
            case "#gethost":
                System.out.println(getHost());

                break;
            case "#getport":
                System.out.println(getPort());
                break;
            default:
                System.out.println("Incorrect Command, you can try:");
                System.out.println("#quit");
                System.out.println("#logoff");
                System.out.println("#sethost <hostName> (or just hostname)");
                System.out.println("#setport <portname> (or just portname)");
                System.out.println("#login");
                System.out.println("#gethost");
                System.out.println("#getport");


        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {

        }
        System.exit(0);
    }

    protected void connectionException(Exception exception) {
        System.out.println("The server is down! Disconnecting from the server.");


    }

    protected void connectionClosed() {
        System.out.println("Connection with the server is closed.");

    }

}
//End of ChatClient class
