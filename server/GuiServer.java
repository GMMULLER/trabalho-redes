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

class ButtonHandlerSendMessageServer implements ActionListener {
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
      System.out.println("ERROR: " + e.getMessage());
    }

    try {
      StyledDocument doc = this.textMessageArea.getStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();
      doc.insertString(doc.getLength(), "Você: " + this.textArea.getText() + "\n", attr);
      this.textArea.setText("");
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
    }
  }
}

class ButtonHandlerConnectServer implements ActionListener {
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
    System.out.println(Integer.parseInt(this.textFieldPort.getText()));
    server.connect(Integer.parseInt(this.textFieldPort.getText()));

    ButtonHandlerSendMessageServer handlerSendMessage = new ButtonHandlerSendMessageServer(this.textArea, this.server.out, this.textMessageArea);
    this.buttonSend.addActionListener(handlerSendMessage);
  }
}

class ButtonHandlerRecordAudio implements ActionListener {

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
    if(this.recording){
      this.button.setText("Gravando");
      this.button.setBackground(Color.RED);
      this.audioRecorder.start();
    }else{
      this.button.setText("Gravar");
      this.button.setBackground(null);
      this.audioRecorder.finish();

      try{
        String absolutePath = "./"+this.audioRecorder.fileName+Integer.toString(this.audioRecorder.countFileName-1)+".wav";

        File file = new File(absolutePath);

        // Create an input stream into the file you want to send.
        FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());

        // Create an output stream to write to the server over the socket connection.
        DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

        String fileName = file.getName();
        // Convert the name of the file into an array of bytes to be sent to the server.
        byte[] fileNameBytes = fileName.getBytes();
        // Create a byte array the size of the file so don't send too little or too much data to the server.
        byte[] fileBytes = new byte[(int)file.length()];
        // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
        fileInputStream.read(fileBytes);
        // Send the length of the name of the file so server knows when to stop reading.
        dataOutputStream.writeInt(2);
        dataOutputStream.writeInt(fileNameBytes.length);
        // Send the file name.
        dataOutputStream.write(fileNameBytes);
        // Send the length of the byte array so the server knows when to stop reading.
        dataOutputStream.writeInt(fileBytes.length);
        // Send the actual file.
        dataOutputStream.write(fileBytes);

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
          System.out.println("ERROR: " + e.getMessage());
        }
      }catch(Exception e){
        System.out.println(e.getMessage());
      }
    }
  }
}

class ButtonHandlerReproduceAudio implements ActionListener {
  public String filename;
  public AudioPlayer audioPlayer;

  public ButtonHandlerReproduceAudio(String filename, AudioPlayer audioPlayer) {
    this.filename = filename;
    this.audioPlayer = audioPlayer;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    // this.audioPlayer.setAudio();
    this.audioPlayer.play(this.filename);
  }
}

class GuiServer extends JFrame {  
  public static void main(String[] args) {  
    AudioPlayer audioPlayer = new AudioPlayer();
    boolean exit = false;

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

    while(server.in == null) {
      System.out.println("Conectando... Tenha paciência!!");
    }

    ButtonHandlerRecordAudio handlerRecord = new ButtonHandlerRecordAudio(buttonRecord, audioRecorder, server.clientSocket, textMessageArea, audioPlayer);
    buttonRecord.addActionListener(handlerRecord);

    try{
      // Stream to receive data from the client through the socket.
      DataInputStream dataInputStream = new DataInputStream(server.clientSocket.getInputStream());

      StyledDocument doc = textMessageArea.getStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();
      while(true) {
        // Read the size of the file name so know when to stop reading.
        int messageType = dataInputStream.readInt();

        if(messageType == 1) {

          try {
            doc.insertString(doc.getLength(), "Outro: " + dataInputStream.readUTF() + "\n", attr);
            textArea.setText("");
          } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
          }

        } else {
          int fileNameLength = dataInputStream.readInt();

          System.out.println(fileNameLength);

          // If the file exists
          if (fileNameLength > 0) {
            // Byte array to hold name of file.
            byte[] fileNameBytes = new byte[fileNameLength];
            // Read from the input stream into the byte array.
            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
            // Create the file name from the byte array.
            String fileName = new String(fileNameBytes);
            System.out.println(fileName);
            // Read how much data to expect for the actual content of the file.
            int fileContentLength = dataInputStream.readInt();

            if (fileContentLength > 0) {
              // Array to hold the file data.
              byte[] fileContentBytes = new byte[fileContentLength];
              // Read from the input stream into the fileContentBytes array.
              dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
              
              // Create the file with its name.
              File fileToDownload = new File(fileName);
              // Create a stream to write data to the file.
              FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
              // Write the actual file data to the file.
              fileOutputStream.write(fileContentBytes);
              // Close the stream.
              fileOutputStream.close();

              try {
                doc.insertString(doc.getLength(), "Outro: ", attr);
                JButton buttonExecuteAudio = new JButton("Reproduzir");
                ButtonHandlerReproduceAudio handlerReproduceAudio = new ButtonHandlerReproduceAudio(fileName, audioPlayer);
                buttonExecuteAudio.addActionListener(handlerReproduceAudio);
                textMessageArea.setCaretPosition(textMessageArea.getDocument().getLength());
                textMessageArea.insertComponent(buttonExecuteAudio);
                doc.insertString(doc.getLength(), "\n", attr);
              } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
              }

            }
          }
        }
      }
    } catch(Exception e){
      System.out.println("Erro: "+e.getMessage());
    }
  }  
}
