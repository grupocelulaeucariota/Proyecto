package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

// Superficie de juego donde se animan y gestionan las bolas.
// Controla la lógica de animación, interacción, tiempo y renderizado.
public class Surface extends JPanel implements KeyListener, MouseListener {
    private Main main;
    private ScheduledExecutorService executor;
    private boolean paused;
    private final List<Ball> balls;
    private final int targetFPS = 60;
    private final Random random = new Random();

    // --- Reloj y mecánica de tiempo ---
    private int timeLeft = 30; // Tiempo inicial en segundos
    private final int timeBonus = 7; // Tiempo que se suma al acertar
    private final int timePenalty = 5; // Tiempo que se resta al fallar
    private long lastSecond = System.currentTimeMillis();
    private boolean gameOver = false; // Añade esta línea

    // Constructor. Inicializa la superficie, crea las bolas y añade los listeners.
    // @param w Ancho de la superficie.
    // @param h Alto de la superficie.
    // @param numBalls Número inicial de bolas.
    public Surface(int numBalls, Main main) {
        this.main = main;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(screenSize);
        setBackground(Color.BLACK);
        balls = new ArrayList<>();
        for (int i = 0; i < numBalls; i++) {
            balls.add(createRandomBall(screenSize.width, screenSize.height));
        }
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        // Key Listener para salir de pantalla completa con ESC
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(Surface.this);
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

    // Crea una bola con propiedades aleatorias.
    // @param w Ancho máximo para la posición.
    // @param h Alto máximo para la posición.
    // @return Nueva instancia de Ball.
    private Ball createRandomBall(int w, int h) {
        int size = random.nextInt(300) + 150; // Tamaño entre 60 y 120
        double x = random.nextDouble() * (w - size);
        double y = random.nextDouble() * (h - size);
        double direction = Math.toRadians(random.nextInt(360));
        double speed = random.nextDouble() * 200 + 100;
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return new Ball(this, x, y, size, direction, speed, color);
    }

    // Inicia el bucle de animación usando ScheduledExecutorService
    public void start() {
        long delay = 1_000_000_000L / targetFPS; // nanosegundos por frame
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!paused) {
                next(delay);
                updateTimer(); // <-- Aquí se sigue llamando en cada frame
                repaint();
            }
        }, 0, delay, java.util.concurrent.TimeUnit.NANOSECONDS);
    }

    // Detiene el bucle de animación de forma segura
    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    // Pausa la animación.
    public synchronized void pause() { paused = true; }

    // Reanuda la animación si estaba pausada.
    public synchronized void resume() { if (paused) { paused = false; notify(); } }

    // Indica si la animación está pausada.
    // @return true si está pausada, false en caso contrario.
    public synchronized boolean isPaused() { return paused; }

    // Actualiza la posición de todas las bolas.
    // @param lapse Tiempo transcurrido desde el último frame (nanosegundos).
    private void next(long lapse) {
        int w = getWidth();
        int h = getHeight();
        for (Ball ball : balls) ball.move(lapse, w, h);
    }

    // Dibuja todas las bolas y el tiempo restante en pantalla.
    // @param g Contexto gráfico.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Ball ball : balls) {
            g2d.setColor(ball.getColor());
            g2d.fillOval((int) ball.getX(), (int) ball.getY(), ball.getSize(), ball.getSize());
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int) ball.getX(), (int) ball.getY(), ball.getSize(), ball.getSize());
        }
        // Dibuja el tiempo restante (esquina superior izquierda)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Tiempo: " + timeLeft, 20, 40);

        // Dibuja las bolas restantes (esquina superior derecha)
        String bolasRestantes = "Bolas: " + balls.size();
        int textWidth = g2d.getFontMetrics().stringWidth(bolasRestantes);
        g2d.drawString(bolasRestantes, getWidth() - textWidth - 20, 40);

        // Si el juego ha terminado, dibuja el mensaje de derrota centrado
        if (gameOver) {
            String msg = "¡Tiempo agotado! Has perdido la partida.";
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            FontMetrics fm = g2d.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int x = (getWidth() - msgWidth) / 2;
            int y = getHeight() / 2;
            // Fondo semitransparente
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(x - 30, y - 50, msgWidth + 60, 80, 30, 30);
            // Texto
            g2d.setColor(Color.RED);
            g2d.drawString(msg, x, y);
        }
    }

    // --- KeyListener methods ---

    @Override public void keyTyped(KeyEvent e) {}

    // Permite pausar o reanudar el juego pulsando la tecla 'P'.
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            synchronized (this) { if (paused) resume(); else pause(); }
        }
    }
    @Override public void keyReleased(KeyEvent e) {}

    // --- Métodos del MouseListener ---

    // Gestiona la mecánica de clics:
    // - Si aciertas la bola más al frente, la elimina y suma tiempo.
    // - Si fallas, añade una bola y resta tiempo.
    @Override
    public void mouseClicked(MouseEvent e) {
        if (paused || timeLeft <= 0) return;

        int mx = e.getX();
        int my = e.getY();

        List<Integer> clickedBalls = new ArrayList<>();
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            double dx = mx - (ball.getX() + ball.getSize() / 2.0);
            double dy = my - (ball.getY() + ball.getSize() / 2.0);
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= ball.getSize() / 2.0) {
                clickedBalls.add(i);
            }
        }

        if (!clickedBalls.isEmpty() && clickedBalls.get(clickedBalls.size() - 1) == balls.size() - 1) {
            balls.remove(balls.size() - 1);
            timeLeft += timeBonus;
            SoundFX.play("src/main/resources/acierto.wav"); // Sonido de acierto
            // Comprobar victoria
            if (balls.isEmpty() && !gameOver) {
                paused = true;
                gameOver = true;
                SwingUtilities.invokeLater(() -> main.showEndPanel(true)); // true = victoria
                return;
            }
        } else {
            balls.add(0, createRandomBall(getWidth(), getHeight()));
            timeLeft -= timePenalty;
            if (timeLeft < 0) timeLeft = 0;
            if (timeLeft == 0 && !gameOver) {
                paused = true;
                gameOver = true;
                SwingUtilities.invokeLater(() -> main.showEndPanel(false)); // false = derrota
            }
            SoundFX.play("src/main/resources/fallo.wav"); // Sonido de fallo
        }
        repaint();
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // Establece el tiempo inicial para el juego.
    // @param t Tiempo en segundos.
    public void setInitialTime(int t) {
        this.timeLeft = t;
    }

    // Actualiza el temporizador del juego cada segundo.
    // Si el tiempo llega a cero, pausa el juego y muestra un mensaje.
    private void updateTimer() {
        long now = System.currentTimeMillis();
        if (!paused && timeLeft > 0 && now - lastSecond >= 1000) {
            timeLeft--;
            lastSecond = now;
            if (timeLeft <= 0 && !gameOver) {
                paused = true;
                gameOver = true;
                SwingUtilities.invokeLater(() -> main.showEndPanel(false)); // <-- Muestra el EndPanel de derrota
            }
        }
    }
}
