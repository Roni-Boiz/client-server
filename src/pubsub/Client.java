/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ronila
 */
public class Client {
    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public Client(Socket socket, BufferedReader reader, BufferedWriter writer, String username) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.username = username;
    }
    
    private void listenAndSendMessages(){
        Scanner input = new Scanner(System.in);
        String message;
        System.out.println("Enter a message to send to the server (or 'terminate' to quit): ");
        try {
            while (!socket.isClosed()) {
                try { 
                    System.out.print(username+">>");
                    if (input.hasNextLine()) {
                        // Get user input and send it to the server
                        message = input.nextLine();
                    
                        if (message.equalsIgnoreCase("terminate")) {
                            writeMessage("terminate");
                            socket.close();
                            break;
                        }

                        if(message.isEmpty()){
                            continue;
                        }
                        
                        writeMessage(username + ": " + message);
                    }

                    // Receive and print the response from the server
                    System.out.println(reader.readLine());
                }
                catch (IOException e) {
                    System.out.println(reader.readLine());
                    break;
                }   
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            input.close();
            stop();
        }   
    }
    
    private void stop() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static void main(String[] args) {
        
        if (args.length != 3) {
            System.out.println("Usage: java Client <server-address> <port> <username>");
            System.exit(1);
        }
        
        int port=8080;
        try {
            port = Integer.parseInt(args[1]);
        } 
        catch (NumberFormatException e) {
            System.err.println("Invalid port number. Using default port: " + port);
        }
        
        String serverAddress = args[0];
        String username = args[2];
        
        try {
            Socket socket = new Socket(serverAddress, port);        
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));    
            
            Client client = new Client(socket, reader, writer, username);
            client.listenAndSendMessages();
            
            // Register a shutdown hook to handle force stop or terminal close
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { 
                    if(!socket.isClosed()){    
                        System.out.println("terminate");
                        writer.write("terminate");
                        socket.close();
                    }
                } 
                catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }));           
              
        } 
        catch (UnknownHostException e) {
            System.err.println("Invalid server address: " + e.getMessage());
        } 
        catch (SocketTimeoutException e) {
            System.err.println("Connection timeout: " + e.getMessage());
        }
        catch (ConnectException e) {
            System.err.println("Connection refused. No server is listening on port " + port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
