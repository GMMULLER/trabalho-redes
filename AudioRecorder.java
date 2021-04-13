import javax.sound.sampled.*;
import java.io.*;
 
public class AudioRecorder {
    private int countFileName = 0;
    private String fileName;
    private File wavFile;

    public AudioRecorder(String fileName) {
      this.fileName = fileName;
      this.wavFile = new File("./"fileName.toString() + countFileName.toString()+".wav");
    }

    // Formato WAVE do arquivo de audio
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
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
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
 
    // Para a gravacao
    void finish() {
        line.stop();
        line.close();

        this.countFileName++; // Incrementando contador do nome dos arquivos de audio
        this.wavFile = new File("./"this.fileName.toString() + this.countFileName.toString()+".wav"); // Fazendo reatribuição da instancia do arquivo para modificar o nome

        System.out.println("Finished");
    }
}