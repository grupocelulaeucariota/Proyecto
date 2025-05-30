package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigPanel extends JPanel implements MouseListener {
    private int numBolas = 10;
    private int tiempo = 30;
    private Rectangle menosBolas = new Rectangle(180, 120, 40, 40);
    private Rectangle masBolas = new Rectangle(330, 120, 40, 40);
    private Rectangle menosTiempo = new Rectangle(180, 200, 40, 40);
    private Rectangle masTiempo = new Rectangle(330, 200, 40, 40);
    private Rectangle playRect = new Rectangle(200, 300, 200, 60);
    private Runnable onPlay;

    // Constructor del panel de configuración
    public ConfigPanel(Runnable onPlay) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenSize);
        setBackground(Color.GRAY);
        this.onPlay = onPlay;
        addMouseListener(this);
        setFocusable(true);

        // Key Listener para salir de pantalla completa con ESC
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(ConfigPanel.this);
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
    }

    // Getters
    public int getNumBolas() { return numBolas; }
    public int getTiempo() { return tiempo; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Centra el título
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String titulo = "Configuración";
        int tituloWidth = g2d.getFontMetrics().stringWidth(titulo);
        g2d.drawString(titulo, (panelWidth - tituloWidth) / 2, panelHeight / 6);

        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        // Calcula posiciones relativas
        int centerX = panelWidth / 2;
        int yBolas = panelHeight / 3;
        int yTiempo = yBolas + 80;
        int yBoton = yTiempo + 120;

        // Etiquetas y valores
        g2d.drawString("Bolas:", centerX - 150, yBolas);
        g2d.drawString(String.valueOf(numBolas), centerX + 30, yBolas);
        g2d.drawString("Tiempo:", centerX - 150, yTiempo);
        g2d.drawString(String.valueOf(tiempo) + " s", centerX + 30, yTiempo);

        // Botones de + y -
        int botonAncho = 40, botonAlto = 40;
        menosBolas = new Rectangle(centerX - 10, yBolas - 30, botonAncho, botonAlto);
        masBolas = new Rectangle(centerX + 80, yBolas - 30, botonAncho, botonAlto);
        menosTiempo = new Rectangle(centerX - 10, yTiempo - 30, botonAncho, botonAlto);
        masTiempo = new Rectangle(centerX + 80, yTiempo - 30, botonAncho, botonAlto);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(menosBolas); g2d.fill(masBolas);
        g2d.fill(menosTiempo); g2d.fill(masTiempo);
        g2d.setColor(Color.BLACK);
        g2d.draw(menosBolas); g2d.draw(masBolas);
        g2d.draw(menosTiempo); g2d.draw(masTiempo);

        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.drawString("-", menosBolas.x + 12, menosBolas.y + 30);
        g2d.drawString("+", masBolas.x + 8, masBolas.y + 30);
        g2d.drawString("-", menosTiempo.x + 12, menosTiempo.y + 30);
        g2d.drawString("+", masTiempo.x + 8, masTiempo.y + 30);

        // Botón JUGAR centrado
        int botonJugarAncho = 200, botonJugarAlto = 60;
        playRect = new Rectangle(centerX - botonJugarAncho / 2, yBoton, botonJugarAncho, botonJugarAlto);
        g2d.setColor(Color.ORANGE);
        g2d.fill(playRect);
        g2d.setColor(Color.BLACK);
        g2d.draw(playRect);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String jugar = "JUGAR";
        int jugarWidth = g2d.getFontMetrics().stringWidth(jugar);
        g2d.drawString(jugar, centerX - jugarWidth / 2, yBoton + 42);

       
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (menosBolas.contains(mx, my) && numBolas > 1) { numBolas--; repaint(); }
        else if (masBolas.contains(mx, my) && numBolas < 50) { numBolas++; repaint(); }
        else if (menosTiempo.contains(mx, my) && tiempo > 5) { tiempo -= 5; repaint(); }
        else if (masTiempo.contains(mx, my) && tiempo < 120) { tiempo += 5; repaint(); }
        else if (playRect.contains(mx, my)) {
            if (onPlay != null) onPlay.run();
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}