package client;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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
        catch (IOException e) { // if cannot connect, tell user and try again
            JOptionPane.showMessageDialog(null,"Currently waiting for connection");
            startConnection();
        }
    }
    
    public void sendNumbers(int[] lotNumbers){        
        try {
            for (int i = 0; i < lotNumbers.length;i++){ // for size of the array, write the numbers to the server
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
        SpinnerNumberModel sModel1 = new SpinnerNumberModel(1, 1, 50, 1);SpinnerNumberModel sModel2 = new SpinnerNumberModel(2, 1, 50, 1);SpinnerNumberModel sModel3 = new SpinnerNumberModel(3, 1, 50, 1);SpinnerNumberModel sModel4 = new SpinnerNumberModel(4, 1, 50, 1);SpinnerNumberModel sModel5 = new SpinnerNumberModel(5, 1, 50, 1);SpinnerNumberModel sModel6 = new SpinnerNumberModel(6, 1, 50, 1); // Create models for jSpinners to use (number between 1-50)
        JSpinner spinner1 = new JSpinner(sModel1);JSpinner spinner2 = new JSpinner(sModel2);JSpinner spinner3 = new JSpinner(sModel3);JSpinner spinner4 = new JSpinner(sModel4);JSpinner spinner5= new JSpinner(sModel5);JSpinner spinner6= new JSpinner(sModel6); // Create jSpinners for use within JOptionPane
        Object[] message = { "Number 1:", spinner1,"Number 2:", spinner2,"Number 3:", spinner3,"Number 4:", spinner4,"Number 5:", spinner5,"Number 6",spinner6}; // Construct structure for JOptionPane
        int option = JOptionPane.showOptionDialog(null, message, "Lottery! Enter 6 numbers", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); // Display OptionDialog and get option from user (OK, cancel, close)
        int lotNumber1 = (int)spinner1.getValue();int lotNumber2 = (int)spinner2.getValue();int lotNumber3 = (int)spinner3.getValue();int lotNumber4 = (int)spinner4.getValue();int lotNumber5 = (int)spinner5.getValue();int lotNumber6 = (int)spinner6.getValue();
        int[] lotNumbers = {lotNumber1,lotNumber2,lotNumber3,lotNumber4,lotNumber5,lotNumber6}; // Array of numbers created from user entered numbers
        
        for (int i = 0; i < lotNumbers.length; i++) { // For the length of the array, check if the number is the same as another one in the array
            for (int k = i + 1; k < lotNumbers.length; k++) {
                if (lotNumbers[i] == lotNumbers[k]) {
                    JOptionPane.showMessageDialog(null, "You entered the same value more than once");
                    getNumbers();
                }
            }
        }
        
        switch (option) {
            case JOptionPane.OK_OPTION: // if user presses ok, get values from spinners and create array with them
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
    
    public void getPasswordAndCheckIfValid(){
        try {
            JPasswordField passwordField = new JPasswordField(10);
            int passwordDialog = JOptionPane.showConfirmDialog(null,passwordField,"Enter Pass (Default: lottery)",JOptionPane.OK_CANCEL_OPTION); // create confirm dialog for password entering (default password lottery)
            switch (passwordDialog){ // break out and continue program if ok button pressed, else exit program
                case JOptionPane.OK_OPTION:
                    break;
                default:
                    System.exit(0);
            }
            
            String password = passwordField.getText(); // gets password from user
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // creates digest object for hashing
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8)); // encode password into byte version of hash
            StringBuilder sb = new StringBuilder();
            for (byte b : encodedhash) { // build a string with the bytes from encoding
                sb.append(String.format("%02x", b));
            }
            
            if (!"be7f94bb10c4be7b447f065e21b301853b84964b94d3bc03dc11732dee212d73".equals(sb.toString())){ // checks if password is = to hashed value of 'lottery', if it isn't then tell user to re-enter password
                JOptionPane.showMessageDialog(null,"The password you entered was incorrect");
                getPasswordAndCheckIfValid();
            }
        } 
        catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args []){
        Client client = new Client();
        client.getPasswordAndCheckIfValid();
        client.startConnection();           
        client.getAck();
        client.getNumbers();
        client.getResult();
        System.exit(0);
    }
}
