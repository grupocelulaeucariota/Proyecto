package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EndPanel extends JPanel implements MouseListener {
    private final String mensaje;
    private final Color colorMensaje;
    private final Runnable onRestart;
    private final String sonido;

    // Constructor del panel final
    public EndPanel(String mensaje, Color colorMensaje, String sonido, Runnable onRestart) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenSize);
        setBackground(Color.DARK_GRAY);
        this.mensaje = mensaje;
        this.colorMensaje = colorMensaje;
        this.sonido = sonido;
        this.onRestart = onRestart;
        addMouseListener(this);
        setFocusable(true);

        // Key Listener para salir de pantalla completa con ESC
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(EndPanel.this);
                    if (window instanceof JFrame) {
                        JFrame frame = (JFrame) window;
                        frame.dispose();
                        frame.setUndecorated(false);
                        frame.setExtendedState(JFrame.NORMAL);
                        frame.setVisible(true);
                    }
                }
            }
        });
        requestFocusInWindow();

        // Reproduce el sonido correspondiente
        SoundFX.play(sonido);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Mensaje centrado
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        g2d.setColor(colorMensaje);
        int msgWidth = g2d.getFontMetrics().stringWidth(mensaje);
        g2d.drawString(mensaje, (panelWidth - msgWidth) / 2, panelHeight / 2);

        // Botón "Volver a jugar"
        String btnText = "VOLVER A JUGAR";
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        int btnWidth = g2d.getFontMetrics().stringWidth(btnText) + 60;
        int btnHeight = 60;
        int btnX = (panelWidth - btnWidth) / 2;
        int btnY = panelHeight / 2 + 80;
        g2d.setColor(Color.ORANGE);
        g2d.fillRoundRect(btnX, btnY, btnWidth, btnHeight, 30, 30);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(btnX, btnY, btnWidth, btnHeight, 30, 30);
        g2d.drawString(btnText, btnX + 30, btnY + 42);

        // Guarda el rectángulo del botón para detectar clics
        playRect = new Rectangle(btnX, btnY, btnWidth, btnHeight);
    }

    private Rectangle playRect;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (playRect != null && playRect.contains(e.getPoint())) {
            if (onRestart != null) onRestart.run();
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}