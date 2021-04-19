import java.io.File;
import java.io.IOException;
 
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
    // Tamanho do buffer pra receber e enviar audio
    private static final int BUFFER_SIZE = 4096;
     
    // Roda o arquivo de audio passado como argumento
    void play(String audioFilePath) {

        File audioFile = new File(audioFilePath);
        
        try {
            // Recupera o input stream do audio passado como argumento
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
 
            // Recupera o formato da stream de audio: taxa de bits por segundo, qualidade da codificacao e se a codificacao possui um bit rate variavel
            AudioFormat format = audioStream.getFormat();
 
            // Guarda informacoes do feed de audio como: formato de audio suportado e tamanho do buffer interno
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
            // Source de dados para o mixer AudioSystem. Permite a escrita de dados
            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
 
            // Abre a linha com um formato especifico, alocando recursos de sistema
            audioLine.open(format);
 
            // Permite a linha fazer operacoes de I/O
            audioLine.start();
             
            byte[] bytesBuffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
 
            while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                // bytesRead: numero de bytes lidos
                audioLine.write(bytesBuffer, 0, bytesRead);
            }
            
            // Esvazia o buffer interno da linha de audio
            audioLine.drain();
            audioLine.close();
            audioStream.close();
             
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
            System.out.println("Erro: " + ex.getMessage());
            ex.printStackTrace();
        }      
    }

}