import java.io.*;
import javax.swing.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonHandlerSendMessageClient implements ActionListener {
  public JTextArea textArea;
  public PrintWriter out;
  public JTextArea textMessageArea;

  public ButtonHandlerSendMessageClient(JTextArea textArea, PrintWriter out, JTextArea textMessageArea) {
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

class ButtonHandlerConnectClient implements ActionListener {
  public Client client;
  public JTextField textFieldIp;
  public JTextField textFieldPort;
  public JTextArea textArea;
  public JButton buttonSend;
  public JTextArea textMessageArea;

  public ButtonHandlerConnectClient(Client client, JTextField textFieldIp, JTextField textFieldPort, JTextArea textArea, JButton buttonSend, JTextArea textMessageArea) {
    this.client = client;
    this.textFieldIp = textFieldIp;
    this.textFieldPort = textFieldPort;
    this.textArea = textArea;
    this.buttonSend = buttonSend;
    this.textMessageArea = textMessageArea;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    client.connect(this.textFieldIp.getText(), Integer.parseInt(this.textFieldPort.getText()));

    ButtonHandlerSendMessageClient handlerSendMessage = new ButtonHandlerSendMessageClient(this.textArea, this.client.out, this.textMessageArea);
    this.buttonSend.addActionListener(handlerSendMessage);
  }
}

class GuiClient extends JFrame {  
  public static void main(String[] args) {  
    boolean exit = false;

    JFrame f = new JFrame(); // Criando o JFrame  

    JTextArea textMessageArea = new JTextArea(); // Criando a area de mensagens
    JTextArea textArea = new JTextArea(); // Criando o campo de texto
    JButton buttonSend = new JButton("Enviar"); // Criando o botao de enviar
    JLabel labelIp = new JLabel("IP: "); // Criando o label do ip
    JTextField textFieldIp = new JTextField(); // Criando o campo de texto do ip
    JLabel labelPort = new JLabel("Porta: "); // Criando o label da porta
    JTextField textFieldPort = new JTextField(); // Criando o campo de texto da porta
    JButton buttonConnect = new JButton("Conectar"); // Criando o botao de conexao

    textMessageArea.setBounds(10, 10, 280, 480); // Especificando x, y, width, height da area de mensagens
    textArea.setBounds(10, 500, 280, 50); // Especificando x, y, width, height do campo de texto
    buttonSend.setBounds(300, 500, 90, 45); // Especificando x, y, width, height do botao de enviar
    labelIp.setBounds(300, 10, 90, 20); // Especificando x, y, width, height do label do ip
    textFieldIp.setBounds(300, 40, 90, 50); // Especificando x, y, width, height do campo de texto do ip
    labelPort.setBounds(300, 100, 90, 20); // Especificando x, y, width, height do label da porta
    textFieldPort.setBounds(300, 130, 90, 50); // Especificando x, y, width, height do campo de texto da porta
    buttonConnect.setBounds(300, 190, 90, 45); // Especificando x, y, width, height do botao de conexao

    JScrollPane scrollPane = new JScrollPane(textMessageArea); // Criando scroll da area de mensagens
    textMessageArea.setEditable(false);

    f.add(textMessageArea); // Adicionando area de mensagens na tela
    f.add(textArea); // Adicionando campo de texto na tela
    f.add(buttonSend); // Adicionando botao na tela
    f.add(labelIp); // Adicionando campo label do ip na tela
    f.add(textFieldIp); // Adicionando campo de texto do ip na tela
    f.add(labelPort); // Adicionando campo label da porta na tela
    f.add(textFieldPort); // Adicionando campo de texto da porta na tela
    f.add(buttonConnect); // Adicionando botao de conexao na tela
              
    f.setSize(400,600); // Setando o tamanho da tela
    f.setLayout(null); // Setando o layout
    f.setVisible(true); // Tornando a tela visivel

    Client client = new Client();

    ButtonHandlerConnectClient handlerConnect = new ButtonHandlerConnectClient(client, textFieldIp, textFieldPort, textArea, buttonSend, textMessageArea);

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

          textMessageArea.append("Outro: " + msgReceived + "\n");
          System.out.println(msgReceived);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } 
  }
}
