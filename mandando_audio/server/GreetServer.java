import java.net.*;
import java.io.*;

public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) {
        try{
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            
            // out = new PrintWriter(clientSocket.getOutputStream(), true);
            // in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // String greeting = in.readLine();
            // if ("hello server".equals(greeting)) {
            //     out.println("hello client");
            // }
            // else {
            //     out.println("unrecognised greeting");
            // }
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }

    public void stop() {
        try{
            // in.close();
            // out.close();
            clientSocket.close();
            serverSocket.close();
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }

    public static void main(String[] args) {
        GreetServer server = new GreetServer();
        server.start(6666);

        try{

        // Stream to receive data from the client through the socket.
        DataInputStream dataInputStream = new DataInputStream(server.clientSocket.getInputStream());
        // Read the size of the file name so know when to stop reading.
        int fileNameLength = dataInputStream.readInt();

        // If the file exists
        if (fileNameLength > 0) {
            // Byte array to hold name of file.
            byte[] fileNameBytes = new byte[fileNameLength];
            // Read from the input stream into the byte array.
            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
            // Create the file name from the byte array.
            String fileName = new String(fileNameBytes);
            // Read how much data to expect for the actual content of the file.
            int fileContentLength = dataInputStream.readInt();

            if (fileContentLength > 0) {
                // Array to hold the file data.
                byte[] fileContentBytes = new byte[fileContentLength];
                // Read from the input stream into the fileContentBytes array.
                dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                
                // Create the file with its name.
                File fileToDownload = new File(fileName);
                // Create a stream to write data to the file.
                FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                // Write the actual file data to the file.
                fileOutputStream.write(fileContentBytes);
                // Close the stream.
                fileOutputStream.close();
            }
        }
        }catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
        }
    }
}