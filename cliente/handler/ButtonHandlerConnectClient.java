package handler;

import javax.swing.*; 
import javax.swing.text.*;   
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.Client;

// Handler para o botao de conexao do socket
public class ButtonHandlerConnectClient implements ActionListener {
  public Client client;
  public JTextField textFieldIp;
  public JTextField textFieldPort;
  public JTextArea textArea;
  public JButton buttonSend;
  public JTextPane textMessageArea;

  public ButtonHandlerConnectClient(Client client, JTextField textFieldIp, JTextField textFieldPort, JTextArea textArea, JButton buttonSend, JTextPane textMessageArea) {
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