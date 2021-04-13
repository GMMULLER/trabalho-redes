import java.io.*;
import javax.swing.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonHandlerSendMessageServer implements ActionListener {
  public JTextArea textArea;
  public PrintWriter out;
  public JTextArea textMessageArea;

  public ButtonHandlerSendMessageServer(JTextArea textArea, PrintWriter out, JTextArea textMessageArea) {
    this.textArea = textArea;
    this.out = out;
    this.textMessageArea = textMessageArea;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    this.out.println(this.textArea.getText());
    this.textMessageArea.append("Você: " + this.textArea.getText() + "\n");
    this.textArea.setText("");
  }
}

class ButtonHandlerConnectServer implements ActionListener {
  public int i;
  public JTextField textFieldPort;
  public Server server;
  public JTextArea textArea;
  public JButton buttonSend;
  public JTextArea textMessageArea;

  public ButtonHandlerConnectServer(Server server, JTextField textFieldPort, JTextArea textArea, JButton buttonSend, JTextArea textMessageArea) {
    this.server = server;
    this.textFieldPort = textFieldPort;
    this.textArea = textArea;
    this.buttonSend = buttonSend;
    this.textMessageArea = textMessageArea;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    System.out.println(Integer.parseInt(this.textFieldPort.getText()));
    server.connect(Integer.parseInt(this.textFieldPort.getText()));

    ButtonHandlerSendMessageServer handlerSendMessage = new ButtonHandlerSendMessageServer(this.textArea, this.server.out, this.textMessageArea);
    this.buttonSend.addActionListener(handlerSendMessage);
  }
}

class GuiServer extends JFrame {  
  public static void main(String[] args) {  
    boolean exit = false;

    JFrame f = new JFrame(); // Criando o JFrame  

    JTextArea textMessageArea = new JTextArea(); // Criando a area de mensagens
    JTextArea textArea = new JTextArea(); // Criando o campo de texto
    JButton buttonSend = new JButton("Enviar"); // Criando o botao de enviar
    JLabel labelPort = new JLabel("Porta: "); // Criando o label da porta
    JTextField textFieldPort = new JTextField(); // Criando o campo de texto da porta
    JButton buttonConnect = new JButton("Conectar"); // Criando o botao de conexao

    textMessageArea.setBounds(10, 10, 280, 480); // Especificando x, y, width, height da area de mensagens
    textArea.setBounds(10, 500, 280, 50); // Especificando x, y, width, height do campo de texto
    buttonSend.setBounds(300, 500, 90, 45); // Especificando x, y, width, height do botao de enviar
    labelPort.setBounds(300, 10, 90, 20); // Especificando x, y, width, height do label da porta
    textFieldPort.setBounds(300, 40, 90, 50); // Especificando x, y, width, height do campo de texto da porta
    buttonConnect.setBounds(300, 100, 90, 45); // Especificando x, y, width, height do botao de conexao
           
    JScrollPane scrollPane = new JScrollPane(textMessageArea); // Criando scroll da area de mensagens
    textMessageArea.setEditable(false);

    f.add(textMessageArea); // Adicionando area de mensagens na tela   
    f.add(textArea); // Adicionando campo de texto na tela
    f.add(buttonSend); // Adicionando botoes na tela
    f.add(labelPort); // Adicionando campo label da porta na tela
    f.add(textFieldPort); // Adicionando campo de texto da porta na tela
    f.add(buttonConnect); // Adicionando botao de conexao na tela
              
    f.setSize(400,600); // Setando o tamanho da tela
    f.setLayout(null); // Setando o layout
    f.setVisible(true); // Tornando a tela visivel

    Server server = new Server(); 

    ButtonHandlerConnectServer handlerConnect = new ButtonHandlerConnectServer(server, textFieldPort, textArea, buttonSend, textMessageArea);

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

          textMessageArea.append("Outro: " + msgReceived + "\n");
          System.out.println(msgReceived);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } 
  }  
}
