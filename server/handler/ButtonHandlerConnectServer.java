package handler;

import javax.swing.*;  
import javax.swing.text.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

import main.Server;

// Handler para o botao de conexao do socket
public class ButtonHandlerConnectServer implements ActionListener {
  public int i;
  public JTextField textFieldPort;
  public Server server;
  public JTextArea textArea;
  public JButton buttonSend;
  public JTextPane textMessageArea;

  public ButtonHandlerConnectServer(Server server, JTextField textFieldPort, JTextArea textArea, JButton buttonSend, JTextPane textMessageArea) {
    this.server = server;
    this.textFieldPort = textFieldPort;
    this.textArea = textArea;
    this.buttonSend = buttonSend;
    this.textMessageArea = textMessageArea;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {

    server.connect(Integer.parseInt(this.textFieldPort.getText()));

    ButtonHandlerSendMessageServer handlerSendMessage = new ButtonHandlerSendMessageServer(this.textArea, this.server.out, this.textMessageArea);
    this.buttonSend.addActionListener(handlerSendMessage);
  }
}