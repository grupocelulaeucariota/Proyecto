package game;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
public class Main extends WindowAdapter {
    private final JFrame frame;
    private Surface surface;
    private MenuPanel menuPanel;
    private ConfigPanel configPanel;
    private int bolasSeleccionadas = 10;
    private int tiempoSeleccionado = 30;
    private MusicPlayer musicPlayer;
    private EndPanel endPanel;

    // Metodo main, inicio de el frame, lector de eventos y estados
    public Main() {
        frame = new JFrame("Bola Rebotando");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);

        // Pantalla completa
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);

        menuPanel = new MenuPanel(100, this::showConfig); // El tamaño se ajustará automáticamente

        frame.add(menuPanel);
        frame.setVisible(true); // Haz visible antes de obtener el tamaño real
        frame.pack();
        frame.setLocationRelativeTo(null);

        musicPlayer = new MusicPlayer();
        musicPlayer.setVolume(0.05f); // % del volumen máximo
        musicPlayer.playLoop("src/main/resources/LEAN.wav");
    }

    // Muestra el panel de configuración
    private void showConfig() {
        frame.remove(menuPanel);
        configPanel = new ConfigPanel(this::startGame);
        frame.add(configPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Inicia el juego con los parámetros seleccionados
    private void startGame() {
        bolasSeleccionadas = configPanel.getNumBolas();
        tiempoSeleccionado = configPanel.getTiempo();
        frame.remove(configPanel);
        surface = new Surface(bolasSeleccionadas, this); // <-- pasa this
        surface.setInitialTime(tiempoSeleccionado);
        frame.add(surface);
        frame.revalidate();
        frame.repaint();
        surface.start();
        surface.requestFocusInWindow();
    }

    public void iniciar() {
        frame.setVisible(true);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (surface != null) surface.stop();
        frame.dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main()::iniciar);
    }

    // Muestra el panel final (victoria o derrota)
    public void showEndPanel(boolean victoria) {
        String mensaje;
        Color color;
        String sonido;
        if (victoria) {
            mensaje = "¡Enhorabuena! ¡Has ganado!";
            color = Color.GREEN;
            sonido = "src/main/resources/victoria.wav";
        } else {
            mensaje = "¡Tiempo agotado! Has perdido la partida.";
            color = Color.RED;
            sonido = "src/main/resources/derrota.wav";
        }
        if (surface != null) surface.stop();
        frame.remove(surface);
        endPanel = new EndPanel(mensaje, color, sonido, this::restartGame);
        frame.add(endPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Método para reiniciar el juego (volver al menú, por ejemplo)
    private void restartGame() {
        frame.remove(endPanel);
        menuPanel = new MenuPanel(10, this::showConfig);
        frame.add(menuPanel);
        frame.revalidate();
        frame.repaint();
    }
}