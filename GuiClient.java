import java.io.*;
import javax.swing.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonHandlerSendMessageClient implements ActionListener {
  public JTextArea textArea;
  public PrintWriter out;

  public ButtonHandlerSendMessageClient(JTextArea textArea, PrintWriter out) {
    this.textArea = textArea;
    this.out = out;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    this.out.println(this.textArea.getText());
  }
}

class ButtonHandlerConnectClient implements ActionListener {
  public Client client;
  public JTextField textFieldIp;
  public JTextField textFieldPort;
  public JTextArea textArea;
  public JButton buttonSend;

  public ButtonHandlerConnectClient(Client client, JTextField textFieldIp, JTextField textFieldPort, JTextArea textArea, JButton buttonSend) {
    this.client = client;
    this.textFieldIp = textFieldIp;
    this.textFieldPort = textFieldPort;
    this.textArea = textArea;
    this.buttonSend = buttonSend;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    client.connect(this.textFieldIp.getText(), Integer.parseInt(this.textFieldPort.getText()));

    ButtonHandlerSendMessageClient handlerSendMessage = new ButtonHandlerSendMessageClient(this.textArea, this.client.out);
    this.buttonSend.addActionListener(handlerSendMessage);
  }
}

class GuiClient extends JFrame {  
  public static void main(String[] args) {  
    boolean exit = false;

    JFrame f = new JFrame(); // Criando o JFrame  
              
    JButton buttonSend = new JButton("Enviar"); // Criando o botao de enviar
    JTextArea textArea = new JTextArea(); // Criando o campo de texto
    JTextField textFieldIp = new JTextField(); // Criando o campo de texto do ip
    JTextField textFieldPort = new JTextField(); // Criando o campo de texto da porta
    JButton buttonConnect = new JButton("Conectar"); // Criando o botao de conexao
    buttonSend.setBounds(130,100,100, 40); // Especificando x, y, width, height do botao de enviar
    textArea.setBounds(130, 200, 200, 50); // Especificando x, y, width, height do campo de texto
    textFieldIp.setBounds(300, 200, 100, 50); // Especificando x, y, width, height do campo de texto do ip
    textFieldPort.setBounds(300, 300, 100, 50); // Especificando x, y, width, height do campo de texto da porta
    buttonConnect.setBounds(300,  350, 100, 40); // Especificando x, y, width, height do botao de conexao
              
    f.add(buttonSend); // Adicionando botao na tela
    f.add(textArea); // Adicionando campo de texto na tela
    f.add(textFieldPort); // Adicionando campo de texto da porta na tela
    f.add(textFieldIp); // Adicionando campo de texto do ip na tela
    f.add(buttonConnect); // Adicionando botao de conexao na tela
              
    f.setSize(400,500); // Setando o tamanho da tela
    f.setLayout(null); // Setando o layout
    f.setVisible(true); // Tornando a tela visivel

    Client client = new Client();

    ButtonHandlerConnectClient handlerConnect = new ButtonHandlerConnectClient(client, textFieldIp, textFieldPort, textArea, buttonSend);

    buttonConnect.addActionListener(handlerConnect);

    while(client.in == null) {
      System.out.println("Conectando... Tenha paciência!!");
    }

    String msgReceived = "";

    while(true) {
      try {
        if ((msgReceived = client.in.readLine()) != null) {
          if (msgReceived.equals("#")) {
            client.out.println("#");
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
