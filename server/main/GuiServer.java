package main;

import java.io.*;
import javax.swing.*;  
import javax.swing.text.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.net.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.util.concurrent.atomic.AtomicBoolean;

import handler.*;
import audio.*;

// Interface grafica do servidor
class GuiServer extends JFrame {

  public static void main(String[] args) {  
    
    // Exclui todos os arquivos residuais de audio da ultima execucao
    File folder = new File("./");
    File fList[] = folder.listFiles();

    for (File f : fList) {
      if (f.getName().endsWith(".wav")) {
        f.delete(); 
      }
    }

    AudioPlayer audioPlayer = new AudioPlayer();

    JFrame f = new JFrame(); // Criando o JFrame  

    AudioRecorder audioRecorder = new AudioRecorder("audioServer");

    JTextPane textMessageArea = new JTextPane(); // Criando a area de mensagens
    JTextArea textArea = new JTextArea(); // Criando o campo de texto
    JButton buttonRecord = new JButton("Gravar"); // Criando botao de gravacao de audio
    JButton buttonSend = new JButton("Enviar"); // Criando o botao de enviar
    JLabel labelPort = new JLabel("Porta: "); // Criando o label da porta
    JTextField textFieldPort = new JTextField(); // Criando o campo de texto da porta
    JButton buttonConnect = new JButton("Conectar"); // Criando o botao de conexao

    buttonRecord.setBounds(300, 440, 90, 45); // Especificando x, y, width, height do botao de gravacao de audio
    textMessageArea.setBounds(10, 10, 280, 480); // Especificando x, y, width, height da area de mensagens
    textArea.setBounds(10, 500, 280, 50); // Especificando x, y, width, height do campo de texto
    buttonSend.setBounds(300, 500, 90, 45); // Especificando x, y, width, height do botao de enviar
    labelPort.setBounds(300, 10, 90, 20); // Especificando x, y, width, height do label da porta
    textFieldPort.setBounds(300, 40, 90, 50); // Especificando x, y, width, height do campo de texto da porta
    buttonConnect.setBounds(300, 100, 90, 45); // Especificando x, y, width, height do botao de conexao
           
    JScrollPane scrollPane = new JScrollPane(textMessageArea); // Criando scroll da area de mensagens
    textMessageArea.setEditable(false);

    f.add(buttonRecord); // Adicionando botao de gravacao de audio na tela
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

    // AtomicBoolean exit = new AtomicBoolean(false);

    f.addWindowListener(new java.awt.event.WindowAdapter(){
      @Override
      public void windowClosing(java.awt.event.WindowEvent windowEvent){
        // exit.set(true);
        if(server.out != null){
          try{
            server.out.writeInt(3);
          }catch(IOException e){
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
          }
        }
        System.exit(0);
      }
    });

    boolean printConnectionMsg = true;

    while(server.in == null) {
      if(printConnectionMsg){
        System.out.println("Aguardando conexão...");
        printConnectionMsg = false;
      }
      System.out.print("");
      // if(exit.get()){
      //   System.exit(0);
      // }
    }
    System.out.println("Conexão estabelecida!");

    ButtonHandlerRecordAudio handlerRecord = new ButtonHandlerRecordAudio(buttonRecord, audioRecorder, server.clientSocket, textMessageArea, audioPlayer);
    buttonRecord.addActionListener(handlerRecord);

    try{
      // Stream de troca de dados entre os dados via socket
      DataInputStream dataInputStream = new DataInputStream(server.clientSocket.getInputStream());

      StyledDocument doc = textMessageArea.getStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();

      while (true) {
        // Faz a leitura do tipo da mensagem
        int messageType = dataInputStream.readInt();

        if(messageType == 3){
          System.exit(0);
        }

        // Se a mensagem for do tipo 1, é do tipo texto
        if(messageType == 1) {
          try {
            doc.insertString(doc.getLength(), "Outro: " + dataInputStream.readUTF() + "\n", attr);
            textArea.setText("");
          } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
          }
        } else {
          // Descobre o tamanho do nome do arquivo
          int fileNameLength = dataInputStream.readInt();

          if (fileNameLength > 0) {
            // Lendo nome do arquivo
            byte[] fileNameBytes = new byte[fileNameLength];
            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
            String fileName = new String(fileNameBytes);

            // Lendo conteudo do arquivo
            int fileContentLength = dataInputStream.readInt();

            if (fileContentLength > 0) {
              
              byte[] fileContentBytes = new byte[fileContentLength];
              dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
              
              // Salvando os valores lidos no arquivo
              File fileToDownload = new File(fileName);
              FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
              fileOutputStream.write(fileContentBytes);
              fileOutputStream.close();

              // Adiciona o botao de execução do audio
              try {
                doc.insertString(doc.getLength(), "Outro: ", attr);
                JButton buttonExecuteAudio = new JButton("Reproduzir");
                ButtonHandlerReproduceAudio handlerReproduceAudio = new ButtonHandlerReproduceAudio(fileName, audioPlayer);
                buttonExecuteAudio.addActionListener(handlerReproduceAudio);
                textMessageArea.setCaretPosition(textMessageArea.getDocument().getLength());
                textMessageArea.insertComponent(buttonExecuteAudio);
                doc.insertString(doc.getLength(), "\n", attr);
              } catch (Exception e) {
                System.out.println("ERrro: " + e.getMessage());
                e.printStackTrace();
              }

            }
          }
        }
      }
    } catch(Exception e){
      System.out.println("Erro: " + e.getMessage());
      e.printStackTrace();
    }
  }  
}
