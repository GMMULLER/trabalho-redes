import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import audio.*;

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