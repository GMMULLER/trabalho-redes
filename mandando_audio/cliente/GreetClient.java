import java.net.*;
import java.io.*;

public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try{

            clientSocket = new Socket(ip, port);
            
            // out = new PrintWriter(clientSocket.getOutputStream(), true);
            // in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }

    public void sendMessage(String msg) {
        try{

            //COLOCAR CAMINHO ABSOLUTO
            String absolutePath = "/home/gustavo/code/redes/cliente/RecordAudio.wav";
            File file = new File(absolutePath);

            // Create an input stream into the file you want to send.
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());

            // Create an output stream to write to the server over the socket connection.
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            String fileName = file.getName();
            // Convert the name of the file into an array of bytes to be sent to the server.
            byte[] fileNameBytes = fileName.getBytes();
            // Create a byte array the size of the file so don't send too little or too much data to the server.
            byte[] fileBytes = new byte[(int)file.length()];
            // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
            fileInputStream.read(fileBytes);
            // Send the length of the name of the file so server knows when to stop reading.
            dataOutputStream.writeInt(fileNameBytes.length);
            // Send the file name.
            dataOutputStream.write(fileNameBytes);
            // Send the length of the byte array so the server knows when to stop reading.
            dataOutputStream.writeInt(fileBytes.length);
            // Send the actual file.
            dataOutputStream.write(fileBytes);

            // out.println(msg);
            // String resp = in.readLine();
            // return resp;
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }

        // return " ";

    }

    public void stopConnection() {
        try{
            // in.close();
            // out.close();
            clientSocket.close();
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }
}