import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

class ClientSendMessage implements Runnable {
  private AtomicBoolean exit;
  private PrintWriter out;

  public ClientSendMessage(AtomicBoolean exit, PrintWriter out) {
    this.exit = exit;
    this.out = out;
  }
  
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

class ClientReceiveMessage implements Runnable {
  private AtomicBoolean exit;
  private BufferedReader in;

  public ClientReceiveMessage(AtomicBoolean exit, BufferedReader in) {
    this.exit = exit;
    this.in = in;
  }

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

public class Client {
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
  private AtomicBoolean exit = new AtomicBoolean( false );

  public Client(String ip, int port){
    try {
      clientSocket = new Socket(ip, port);
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
    
    // System.out.print("Para iniciar a conexão, digite o ip do servidor: ");
    // String ip = scanner.nextLine();

    // System.out.print("Para iniciar o conexão, digite o número da porta do servidor: ");
    // int port = scanner.nextInt();
    
    // scanner.nextLine(); //Limpando o buffer

    // Client client = new Client(ip, port);
    Client client = new Client("localhost", 6666);

    ClientReceiveMessage clientReceiveMessage = new ClientReceiveMessage(client.exit, client.in);
    Thread t1 = new Thread(clientReceiveMessage);
    t1.start();

    ClientSendMessage clientSendMessage = new ClientSendMessage(client.exit, client.out);
    Thread t2 = new Thread(clientSendMessage);
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
    //   client.clientSocket.close();
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }
  }
}