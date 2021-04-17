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

// Handler para o botao de envio da mensagem de texto
class ButtonHandlerSendMessageClient implements ActionListener {
  public JTextArea textArea;
  public DataOutputStream out;
  public JTextPane textMessageArea;

  public ButtonHandlerSendMessageClient(JTextArea textArea, DataOutputStream out, JTextPane textMessageArea) {
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

// Handler para o botao de conexao do socket
class ButtonHandlerConnectClient implements ActionListener {
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

// Handler do botao para gravar audio
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

// Handler do botao de reproducao de audio
class ButtonHandlerReproduceAudio implements ActionListener {
  public String filename;
  public AudioPlayer audioPlayer;

  public ButtonHandlerReproduceAudio(String filename, AudioPlayer audioPlayer) {
    this.filename = filename;
    this.audioPlayer = audioPlayer;
  }

  // Trata o evento do botão
  public void actionPerformed(ActionEvent event) {
    this.audioPlayer.play(this.filename);
  }
}

// Interface grafica do cliente
class GuiClient extends JFrame {  
  public static void main(String[] args) {  
    AudioPlayer audioPlayer = new AudioPlayer();
    boolean exit = false;

    JFrame f = new JFrame(); // Criando o JFrame  

    AudioRecorder audioRecorder = new AudioRecorder("audioCliente");

    JTextPane textMessageArea = new JTextPane(); // Criando a area de mensagens
    JTextArea textArea = new JTextArea(); // Criando o campo de texto
    JButton buttonRecord = new JButton("Gravar"); // Criando botao de gravacao de audio
    JButton buttonSend = new JButton("Enviar"); // Criando o botao de enviar
    JLabel labelIp = new JLabel("IP: "); // Criando o label do ip
    JTextField textFieldIp = new JTextField(); // Criando o campo de texto do ip
    JLabel labelPort = new JLabel("Porta: "); // Criando o label da porta
    JTextField textFieldPort = new JTextField(); // Criando o campo de texto da porta
    JButton buttonConnect = new JButton("Conectar"); // Criando o botao de conexao

    buttonRecord.setBounds(300, 440, 90, 45); // Especificando x, y, width, height do botao de gravacao de audio
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

    f.add(buttonRecord); // Adicionando botao de gravacao de audio na tela
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

    ButtonHandlerRecordAudio handlerRecord = new ButtonHandlerRecordAudio(buttonRecord, audioRecorder, client.clientSocket, textMessageArea, audioPlayer);
    buttonRecord.addActionListener(handlerRecord);

    try{
      // Stream de troca de dados entre os dados via socket
      DataInputStream dataInputStream = new DataInputStream(client.clientSocket.getInputStream());

      StyledDocument doc = textMessageArea.getStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();
      while (true) {
        // Faz a leitura do tipo da mensagem
        int messageType = dataInputStream.readInt();

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
                System.out.println("Erro: " + e.getMessage());
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
