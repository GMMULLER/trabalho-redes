import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

class ServerSendMessage implements Runnable {
  private AtomicBoolean exit;
  private PrintWriter out;

  public ServerSendMessage(AtomicBoolean exit, PrintWriter out) {
    this.exit = exit;
    this.out = out;
  }

  @Override
  public void run() {

    Scanner scanner = new Scanner(System.in);

    String msgSent = "";

    while(!this.exit.get()) {
      msgSent = scanner.nextLine();
      System.out.print("\u001B[32m" + "Você: ");
      System.out.println(msgSent + "\u001B[0m");
      this.out.println(msgSent);

      if(msgSent.equals("#")) {
        this.stop();
      }
    } 

    // this.out.close();

  } 

  public void stop() {
    this.exit.set(true);
  }
}

class ServerReceiveMessage implements Runnable {
  private AtomicBoolean exit;
  private BufferedReader in;

  public ServerReceiveMessage(AtomicBoolean exit, BufferedReader in) {
    this.exit = exit;
    this.in = in;
  }

  @Override
  public void run() {

    String msgReceived = "";

    while(!this.exit.get()) {
      try {
        if ((msgReceived = this.in.readLine()) != null) {
          if (msgReceived.equals("#")) {
            this.stop();
          }

          System.out.println("\u001B[36m" + "Outro: " + msgReceived + "\u001B[0m");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } 

    // try {
    //   this.in.close();
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }

  }

  public void stop() {
    this.exit.set(true);
  } 
}

public class Server {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
  AtomicBoolean exit = new AtomicBoolean( false );

  public Server(int port){
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      clientSocket = serverSocket.accept();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out = new PrintWriter(clientSocket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    
    // System.out.print("Para iniciar o servidor, digite o número de uma porta: ");
    // int port = scanner.nextInt();

    // Server server = new Server(port);
    Server server = new Server(6666);

    ServerReceiveMessage serverReceiveMessage = new ServerReceiveMessage(server.exit, server.in);
    Thread t1 = new Thread(serverReceiveMessage);
    t1.start();

    ServerSendMessage serverSendMessage = new ServerSendMessage(server.exit, server.out);
    Thread t2 = new Thread(serverSendMessage);
    t2.start();

    // try {
    //   t1.join();
    // } catch (InterruptedException e) {
    //   e.printStackTrace();
    // }

    // try {
    //   t2.join();
    // } catch (InterruptedException e) {
    //   e.printStackTrace();
    // }

    // try {
    //   server.clientSocket.close();
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }

    // try {
    //   server.serverSocket.close();
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }   
  }
}