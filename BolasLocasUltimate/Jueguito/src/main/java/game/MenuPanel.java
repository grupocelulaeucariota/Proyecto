package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MenuPanel extends JPanel implements MouseListener {
    private final List<Ball> balls;
    private final Random random = new Random();
    private Thread t;
    private boolean running = true;
    private Rectangle playButtonRect;
    private Rectangle exitButtonRect;
    private Rectangle aboutButtonRect;
    private Runnable onPlay; // Acción al pulsar "Jugar"

    // Constructor del menú principal
    public MenuPanel(int numBalls, Runnable onPlay) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenSize);
        setBackground(Color.DARK_GRAY);
        balls = new ArrayList<>();
        for (int i = 0; i < numBalls; i++) {
            balls.add(createRandomBall(screenSize.width, screenSize.height));
        }
        this.onPlay = onPlay;
        addMouseListener(this);
        setFocusable(true);
        startAnimation();

        // Key Listener para cerrar el menú 
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(MenuPanel.this);
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
    }

    // Crea una bola aleatoria
    private Ball createRandomBall(int w, int h) {
        int size = random.nextInt(61) + 120;
        double x = random.nextDouble() * (w - size);
        double y = random.nextDouble() * (h - size);
        double direction = Math.toRadians(random.nextInt(360));
        double speed = random.nextDouble() * 200 + 100;
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return new Ball(null, x, y, size, direction, speed, color);
    }

    // Inicia la animación de las bolas
    private void startAnimation() {
        t = new Thread(() -> {
            long t0 = System.nanoTime();
            while (running) {
                long t1 = System.nanoTime();
                long lapse = t1 - t0;
                int w = getWidth();
                int h = getHeight();
                for (Ball ball : balls) ball.move(lapse, w, h);
                repaint();
                t0 = t1;
                try { Thread.sleep(16); } catch (InterruptedException e) { running = false; }
            }
        });
        t.start();
    }

    // Detiene la animación
    public void stopAnimation() {
        running = false;
        if (t != null) t.interrupt();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuja las bolas
        for (Ball ball : balls) {
            g2d.setColor(ball.getColor());
            g2d.fillOval((int) ball.getX(), (int) ball.getY(), ball.getSize(), ball.getSize());
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int) ball.getX(), (int) ball.getY(), ball.getSize(), ball.getSize());
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Título centrado
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Impact", Font.BOLD, 64));
        String titulo = "BOLAS LOCAS";
        FontMetrics fm = g2d.getFontMetrics();
        int tituloWidth = fm.stringWidth(titulo);
        int tituloX = (panelWidth - tituloWidth) / 2;
        int tituloY = panelHeight / 6;

        // Sombra
        g2d.setColor(Color.BLACK);
        g2d.drawString(titulo, tituloX + 4, tituloY + 4);

        // Degradado
        GradientPaint gp = new GradientPaint(
            tituloX, tituloY - 50, Color.YELLOW,
            tituloX + tituloWidth, tituloY + 20, Color.ORANGE
        );
        g2d.setPaint(gp);
        g2d.drawString(titulo, tituloX, tituloY);

        int botonAncho = 150, botonAlto = 50;
        int espacio = 30;

        int botonX = (panelWidth - botonAncho) / 2;
        int botonY = (int) (panelHeight * 0.7);

        // Botón JUGAR
        playButtonRect = new Rectangle(botonX, botonY, botonAncho, botonAlto);

        // Botón ACERCA DE (debajo de JUGAR)
        aboutButtonRect = new Rectangle(botonX, botonY + botonAlto + espacio, botonAncho, botonAlto);

        // Botón SALIR (debajo de ACERCA DE)
        exitButtonRect = new Rectangle(botonX, botonY + 2 * (botonAlto + espacio), botonAncho, botonAlto);

        // Dibuja los botones
        g2d.setColor(Color.ORANGE);
        g2d.fill(playButtonRect);
        g2d.fill(aboutButtonRect);
        g2d.fill(exitButtonRect);

        g2d.setColor(Color.BLACK);
        g2d.draw(playButtonRect);
        g2d.draw(aboutButtonRect);
        g2d.draw(exitButtonRect);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String jugar = "JUGAR";
        String acerca = "ACERCA DE";
        String salir = "SALIR";
        int jugarWidth = g2d.getFontMetrics().stringWidth(jugar);
        int acercaWidth = g2d.getFontMetrics().stringWidth(acerca);
        int salirWidth = g2d.getFontMetrics().stringWidth(salir);

        g2d.drawString(jugar, botonX + (botonAncho - jugarWidth) / 2, botonY + 35);
        g2d.drawString(acerca, botonX + (botonAncho - acercaWidth) / 2, botonY + botonAlto + espacio + 35);
        g2d.drawString(salir, botonX + (botonAncho - salirWidth) / 2, botonY + 2 * (botonAlto + espacio) + 35);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        if (playButtonRect.contains(mx, my)) {
            stopAnimation();
            if (onPlay != null) onPlay.run();
            return;
        }
        if (aboutButtonRect.contains(mx, my)) {
            showAboutDialog();
            return;
        }
        if (exitButtonRect.contains(mx, my)) {
            System.exit(0);
            return;
        }
        // Clic sobre bolas
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            double dx = mx - (ball.getX() + ball.getSize() / 2.0);
            double dy = my - (ball.getY() + ball.getSize() / 2.0);
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= ball.getSize() / 2.0) {
                balls.remove(i);
                repaint();
                return;
            }
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // Muestra el diálogo "Acerca de"
    private void showAboutDialog() {
    Frame parent = JOptionPane.getFrameForComponent(this);
    JDialog dialog = new JDialog(parent, "Acerca de", true);
    dialog.setSize(500, 260);
    dialog.setLocationRelativeTo(this);

    String html = "<html><body style='background-color: #FFFFDC; font-family: Arial; font-size: 16px; text-align: justify;'>" +
            "<h2 style='text-align:center;'>Bolas Locas</h2>" +
            "<p><b>Juego realizado por Mario, María, Mario y Jorge</b></p>" +
            "<p>La mecánica del juego consiste en hacer desaparecer todas las bolas sin que el tiempo se acabe. " +
            "Deberás encontrar la bola que más alante está en la pantalla para ganar.<br><br>¡Gracias por jugar!</p>" +
            "</body></html>";

    JEditorPane editorPane = new JEditorPane("text/html", html);
    editorPane.setEditable(false);
    editorPane.setBackground(new Color(255, 255, 220));
    editorPane.setBorder(null);

    dialog.add(editorPane);
    dialog.setVisible(true);
}
}