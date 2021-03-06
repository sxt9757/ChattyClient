import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChattyClientThread implements Runnable{
    private Socket client;
    private BufferedReader in;
    private TextArea console;
    private BorderPane gui;
    public ChattyClientThread(Socket client, BorderPane gui){
        this.console=((TextArea)gui.getCenter());
        this.gui=gui;
        this.client=client;
        try {
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }
        catch (UnknownHostException ex){
            System.err.println("Error: Unknown Host!");
        }
        catch (IOException ex){
            System.err.println("Error: port must be numerical!");
        }
    }
    public void run(){
        while (true){
            try {
                String serverResponse = this.in.readLine();
                if (serverResponse!=null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            console.appendText(serverResponse + "\n");

                        }
                    });
                    String[] parsedResponse = serverResponse.split(" ");
                    if (parsedResponse[0].equals("Server:") && parsedResponse[1].equals("Disconnected")) {
                        System.out.println("hi");
                        try {
                            this.in.close();
                        }
                        catch (IOException ex){
                            ex.printStackTrace();
                        }
                        break;
                    }
                }
                else{
                    throw new SocketException();
                }
            }
            catch (SocketException ex){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ((Label)gui.getTop()).setText("Disconnected!");
                    }
                });
                break;
            }
            catch(IOException ex){
                ex.printStackTrace();
                break;
            }
        }
    }
}
