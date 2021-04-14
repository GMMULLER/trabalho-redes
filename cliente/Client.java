import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
  public Socket clientSocket;
  public DataOutputStream out;
  public BufferedReader in;

  public Client() {
    this.in = null;
  }

  public void connect(String ip, int port) {
    try {
      this.clientSocket = new Socket(ip, port);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      this.out = new DataOutputStream(this.clientSocket.getOutputStream());
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