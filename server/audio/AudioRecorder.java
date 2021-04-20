package audio;

import javax.sound.sampled.*;
import java.io.*;
 
public class AudioRecorder {
    public int countFileName = 0;
    public String fileName;
    public File wavFile;

    public AudioRecorder(String fileName) {
      this.fileName = fileName;
      this.wavFile = new File("./"+fileName.toString() + Integer.toString(countFileName)+".wav");
    }

    // Definindo WAVE como formato de audio
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // Linha de audio capturada por um dispositivo de audio
    TargetDataLine line;
 
    // Definindo configurações do audio
    AudioFormat getAudioFormat() {
      float sampleRate = 16000;
      int sampleSizeInBits = 8;
      int channels = 2;
      boolean signed = true;
      boolean bigEndian = true;
      AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                            channels, signed, bigEndian);
      return format;
    }
 
    // Inicia a gravacao
    public void start() {
        Thread threadRecorder = new Thread(new Runnable() {
            public void run() {
                try {
                    AudioFormat format = getAudioFormat();

                    // Guarda informacoes do feed de audio como: formato de audio suportado e tamanho do buffer interno
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
                    if (!AudioSystem.isLineSupported(info)) {
                        System.out.println("Linha não suportada");
                        System.exit(0);
                    }

                    line = (TargetDataLine) AudioSystem.getLine(info);

                    // Abre a linha com um formato especifico, alocando recursos de sistema
                    line.open(format);

                    // Permite a linha fazer operacoes de I/O
                    line.start(); 
        
                    AudioInputStream ais = new AudioInputStream(line);
        
                    // Comeca a escrever no arquivo de audio
                    AudioSystem.write(ais, fileType, wavFile);
        
                } catch (LineUnavailableException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        threadRecorder.start();
    }
 
    // Para a gravacao
    public void finish() {
        line.stop();
        line.close();

        // Incrementando contador do nome dos arquivos de audio
        this.countFileName++; 
        // Fazendo reatribuição da instancia do arquivo para modificar o nome
        this.wavFile = new File("./"+this.fileName.toString() + Integer.toString(this.countFileName)+".wav");
    }
}