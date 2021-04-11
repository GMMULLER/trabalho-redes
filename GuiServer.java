import java.io.*;
import javax.swing.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonHandlerSendMessageServer implements ActionListener {
  public JTextArea textArea;
  public PrintWriter out;

  public ButtonHandlerSendMessageServer(JTextArea textArea, PrintWriter out) {
    this.textArea = textArea;
    this.out = out;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    this.out.println(this.textArea.getText());
  }
}

class ButtonHandlerConnectServer implements ActionListener {
  public int i;
  public JTextField textFieldPort;
  public Server server;
  public JTextArea textArea;
  public JButton buttonSend;

  public ButtonHandlerConnectServer(Server server, JTextField textFieldPort, JTextArea textArea, JButton buttonSend) {
    this.server = server;
    this.textFieldPort = textFieldPort;
    this.textArea = textArea;
    this.buttonSend = buttonSend;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    System.out.println(Integer.parseInt(this.textFieldPort.getText()));
    server.connect(Integer.parseInt(this.textFieldPort.getText()));

    ButtonHandlerSendMessageServer handlerSendMessage = new ButtonHandlerSendMessageServer(this.textArea, this.server.out);
    this.buttonSend.addActionListener(handlerSendMessage);
  }
}

class GuiServer extends JFrame {  
  public static void main(String[] args) {  
    boolean exit = false;

    JFrame f = new JFrame(); // Criando o JFrame  
              
    JButton buttonSend = new JButton("Enviar"); // Criando o botao de enviar
    JTextArea textArea = new JTextArea(); // Criando o campo de texto
    JTextField textFieldPort = new JTextField(); // Criando o campo de texto da porta
    JButton buttonConnect = new JButton("Conectar"); // Criando o botao de conexao
    buttonSend.setBounds(130,100,100, 40); // Especificando x, y, width, height do botao de enviar
    textArea.setBounds(130, 200, 200, 50); // Especificando x, y, width, height do campo de texto
    textFieldPort.setBounds(300, 200, 100, 50); // Especificando x, y, width, height do campo de texto da porta
    buttonConnect.setBounds(300,  350, 100, 40); // Especificando x, y, width, height do botao de conexao
              
    f.add(buttonSend); // Adicionando botoes na tela
    f.add(textArea); // Adicionando campo de texto na tela
    f.add(textFieldPort); // Adicionando campo de texto da porta na tela
    f.add(buttonConnect); // Adicionando botao de conexao na tela
              
    f.setSize(400,500); // Setando o tamanho da tela
    f.setLayout(null); // Setando o layout
    f.setVisible(true); // Tornando a tela visivel

    Server server = new Server(); 

    ButtonHandlerConnectServer handlerConnect = new ButtonHandlerConnectServer(server, textFieldPort, textArea, buttonSend);

    buttonConnect.addActionListener(handlerConnect);

    while(server.in == null) {
      System.out.println("Conectando... Tenha paciência!!");
    }

    String msgReceived = "";

    while(true) {
      try {
        if ((msgReceived = server.in.readLine()) != null) {
          if (msgReceived.equals("#")) {
            server.out.println("#");
            break;
          }

          System.out.println(msgReceived);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } 
  }  
}
