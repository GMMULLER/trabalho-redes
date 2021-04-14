import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
  public ServerSocket serverSocket;
  public Socket clientSocket;
  public PrintWriter out;
  public BufferedReader in;

  public Server() {
    this.in = null;
  }

  public void connect(int port) {
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      this.clientSocket = serverSocket.accept();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}