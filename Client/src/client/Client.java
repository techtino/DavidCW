package client;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.util.Arrays;

/**
 *
 * @author techtino
 */
public class Client {
    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;

    public void startConnection() {
        try { // attempt to connect to server, sets output stream to support integers (lottery numbers), sets input stream to support Strings(result).
            clientSocket = new Socket("localhost", 8888);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } 
        catch (IOException e) { // if cannot connect, tell user.
            JOptionPane.showMessageDialog(null,"Currently waiting for connection");
            startConnection();
        }
    }
    
    public void sendNumbers(int[] lotNumbers){        
        try {
            for (int i = 0; i <=5;i++){
                out.writeInt(lotNumbers[i]);
            }
        } 
        catch (IOException ex) {
        }
    }

    public String getResult(){
        String result = null;
        try { //read result for winning/losing or incorrect values
            result = in.readLine();
            if (!"At least one of the numbers are not valid, please choose between 1 to 50.".equals(result)){ //if all numbers were valid
                CloseConnection();
            }
            else{ //if numbers werent valid, try get them again
                getNumbers();
            }
        } 
        catch (IOException ex) {
        }
        JOptionPane.showMessageDialog(null,result);
        return result;
    }
    
    public String getAck(){
        String Ack = null;
        try {//read acknowlegement string from server and display to user
            Ack = in.readLine();
            JOptionPane.showMessageDialog(null,Ack);
        } 
        catch (Exception ex) {
        }
        return Ack;
    }
    
    public void CloseConnection(){
        try{// close input stream, output stream and socket
            in.close();
            out.close();
            clientSocket.close();
        }
        catch (IOException ex){   
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    public void getNumbers(){
        SpinnerNumberModel sModel1 = new SpinnerNumberModel(1, 1, 50, 1);SpinnerNumberModel sModel2 = new SpinnerNumberModel(1, 1, 50, 1);SpinnerNumberModel sModel3 = new SpinnerNumberModel(1, 1, 50, 1);SpinnerNumberModel sModel4 = new SpinnerNumberModel(1, 1, 50, 1);SpinnerNumberModel sModel5 = new SpinnerNumberModel(1, 1, 50, 1);SpinnerNumberModel sModel6 = new SpinnerNumberModel(1, 1, 50, 1); // Create models for jSpinners to use (number between 1-50)
        JSpinner spinner1 = new JSpinner(sModel1);JSpinner spinner2 = new JSpinner(sModel2);JSpinner spinner3 = new JSpinner(sModel3);JSpinner spinner4 = new JSpinner(sModel4);JSpinner spinner5= new JSpinner(sModel5);JSpinner spinner6= new JSpinner(sModel6); // Create jSpinners for use within JOptionPane
        Object[] message = { "Number 1:", spinner1,"Number 2:", spinner2,"Number 3:", spinner3,"Number 4:", spinner4,"Number 5:", spinner5,"Number 6",spinner6}; // Construct structure for JOptionPane
        int option = JOptionPane.showOptionDialog(null, message, "Lottery! Enter 6 numbers", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); // Display OptionDialog and get option from user (OK, cancel, close)
        switch (option) {
            case JOptionPane.OK_OPTION: // if user presses ok, get values from spinners and create array with them
                int lotNumber1 = (int)spinner1.getValue();int lotNumber2 = (int)spinner2.getValue();int lotNumber3 = (int)spinner3.getValue();int lotNumber4 = (int)spinner4.getValue();int lotNumber5 = (int)spinner5.getValue();int lotNumber6 = (int)spinner6.getValue();
                int[] lotNumbers = {lotNumber1,lotNumber2,lotNumber3,lotNumber4,lotNumber5,lotNumber6};
                sendNumbers(lotNumbers);
                break;
            default: { // any other option closes connection and closes client
                try {
                    out.writeInt(0); //send close message to server
                    CloseConnection(); //close connection on client side
                    System.exit(0); //close java app
                } 
                catch (IOException ex) {
                }
            }
        }
    }

    public static void main(String args []){
            Client client = new Client();
            client.startConnection();           
            client.getAck();
            client.getNumbers();
            client.getResult();
            System.exit(0);
        } 
    }


