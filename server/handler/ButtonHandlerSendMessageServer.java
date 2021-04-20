package handler;

import java.io.*;
import javax.swing.*; 
import javax.swing.text.*;   
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Handler para o botao de envio da mensagem de texto
public class ButtonHandlerSendMessageServer implements ActionListener {
  public JTextArea textArea;
  public DataOutputStream out;
  public JTextPane textMessageArea;

  public ButtonHandlerSendMessageServer(JTextArea textArea, DataOutputStream out, JTextPane textMessageArea) {
    this.textArea = textArea;
    this.out = out;
    this.textMessageArea = textMessageArea;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    try {
      this.out.writeInt(1);
      this.out.writeUTF(this.textArea.getText());
    } catch (Exception e) {
      System.out.println("Erro: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      StyledDocument doc = this.textMessageArea.getStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();
      doc.insertString(doc.getLength(), "Você: " + this.textArea.getText() + "\n", attr);
      this.textArea.setText("");
    } catch (Exception e) {
      System.out.println("Erro: " + e.getMessage());
      e.printStackTrace();
    }
  }
}