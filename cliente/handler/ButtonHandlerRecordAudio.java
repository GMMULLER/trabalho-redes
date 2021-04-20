package handler;

import java.io.*;
import javax.swing.*; 
import javax.swing.text.*;   
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.net.*;

import audio.*;

// Handler do botao para gravar audio
public class ButtonHandlerRecordAudio implements ActionListener {

  public boolean recording = false;
  public JButton button;
  public AudioRecorder audioRecorder;
  public Socket socket;
  public AudioPlayer audioPlayer;
  public JTextPane textMessageArea;

  public ButtonHandlerRecordAudio(JButton button, AudioRecorder audioRecorder, Socket socket, JTextPane textMessageArea, AudioPlayer audioPlayer) {
    this.button = button;
    this.audioRecorder = audioRecorder;
    this.socket = socket;
    this.audioPlayer = audioPlayer;
    this.textMessageArea = textMessageArea;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    this.recording = !this.recording;

    if (this.recording) {
      this.button.setText("Gravando");
      this.button.setBackground(Color.RED);
      this.audioRecorder.start();
    } else {
      this.button.setText("Gravar");
      this.button.setBackground(null);
      this.audioRecorder.finish();

      try {
        String absolutePath = "./"+this.audioRecorder.fileName+Integer.toString(this.audioRecorder.countFileName-1)+".wav";

        File file = new File(absolutePath);

        // Criando a Stream de entrada do arquivo que sera enviado
        FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());

        // Criando a Stream de saida para enviar pelo socket
        DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

        // Pegando as informações do arquivo para enviar...
        String fileName = file.getName();
        byte[] fileNameBytes = fileName.getBytes();
        byte[] fileBytes = new byte[(int)file.length()];
        fileInputStream.read(fileBytes);
        
        // Enviando para a outra ponta da conexao que o tipo da mensagem eh de audio        
        dataOutputStream.writeInt(2);
        // Enviando tamanho do nome do arquivo
        dataOutputStream.writeInt(fileNameBytes.length);
        // Enviando o nome do arquivo
        dataOutputStream.write(fileNameBytes);
        // Enviando o tamanho do arquivo
        dataOutputStream.writeInt(fileBytes.length);
        // Enviando o arquivo de audio
        dataOutputStream.write(fileBytes);

        // Adicionando botão para reproducao do audio
        try {

          StyledDocument doc = this.textMessageArea.getStyledDocument();
          SimpleAttributeSet attr = new SimpleAttributeSet();
          doc.insertString(doc.getLength(), "Você: ", attr);
          
          JButton buttonExecuteAudio = new JButton("Reproduzir");
          ButtonHandlerReproduceAudio handlerReproduceAudio = new ButtonHandlerReproduceAudio(fileName, this.audioPlayer);
          buttonExecuteAudio.addActionListener(handlerReproduceAudio);
          
          this.textMessageArea.setCaretPosition(this.textMessageArea.getDocument().getLength());
          this.textMessageArea.insertComponent(buttonExecuteAudio);
          doc.insertString(doc.getLength(), "\n", attr);
          
        } catch (Exception e) {
          System.out.println("Erro: " + e.getMessage());
          e.printStackTrace();
        }
      } catch (Exception e) {
        System.out.println("Erro: " + e.getMessage());
        e.printStackTrace();
      }

    }
  }
}