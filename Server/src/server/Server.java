/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author techtino
 */
public class Server {
    public static void main(String[] args) throws IOException{ // starts server and creates new thread when it connects to client
        ServerSocket serverSocket = new ServerSocket(8888);
        int id = 0;
        while(true){
            Socket clientSocket = serverSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
            cliThread.start();
        }
    }
}

class ClientServiceThread extends Thread{ // thread client info
    Socket clientSocket;
    int clientID = -1;
    boolean running = true;
    
    ClientServiceThread(Socket s, int i){
        clientSocket = s;
        clientID = 1;
    }
    @Override
    public void run(){ //runs when thread starts/user connected
        System.out.println("A client has connected");
        try{
            DataInputStream in = new DataInputStream(clientSocket.getInputStream()); // creates input data stream for reading integers from client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream()); // creates output data stream for sending strings to client
            out.println("Hello! You have connected to the lottery server"); // sending acknowlege mesage to client
            out.flush(); 
            while(running){ // running infinitely until told to stop
                Random random = new Random();
                int randomLotteryNum = random.nextInt(50) + 1; // random integer between 1-50 for lottery number
                for(int i = 0; i <= 5;i++){ // runs 6 times for 6 lottery numbers from client
                    int number = in.readInt(); 
                    if (number >=1 & number <=50){ // checks if user number is between 1 and 50
                        if (number == randomLotteryNum){ // if the user entered number is = to the random lottery number
                            out.println("You won! The winning number was: " + randomLotteryNum); //send winning message
                            out.flush(); 
                            out.close(); 
                            in.close(); // closing all streams
                            clientSocket.close(); // disconnecting client
                            System.out.println("A client has disconnected");
                            running=false; // stop thread
                        }
                  }
                    else if (number == 0){ // if server sees exit code then close all streams and close connection
                        System.out.println("Exit code sent by client, closing");
                        out.close();
                        in.close();
                        clientSocket.close();
                        running=false; // stop thread
                    }
                    else{
                        out.println("At least one of the numbers are not valid, please choose between 1 to 50.");
                        out.flush();
                    }
                }
                out.println("You lost! The winning number was: " + randomLotteryNum); // send message to client that they lost
                out.flush();
                out.close();
                in.close(); 
                clientSocket.close();
                System.out.println("A client has disconnected");
                running=false; // stop thread
           }
        }
        catch (IOException ex){
        }
    }   
}
    
